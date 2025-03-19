package com.devsquad10.product.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsquad10.product.application.client.CompanyClient;
import com.devsquad10.product.application.dto.ProductReqDto;
import com.devsquad10.product.application.dto.ProductResDto;
import com.devsquad10.product.application.dto.message.StockDecrementMessage;
import com.devsquad10.product.application.dto.message.StockReversalMessage;
import com.devsquad10.product.application.exception.ProductNotFoundException;
import com.devsquad10.product.domain.enums.ProductStatus;
import com.devsquad10.product.domain.model.Product;
import com.devsquad10.product.domain.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

	@Value("${stockMessage.queue.stock.response}")
	public String queueResponseStock;

	private final ProductRepository productRepository;
	private final RabbitTemplate rabbitTemplate;
	private final CompanyClient companyClient;

	@CachePut(cacheNames = "productCache", key = "#result.id")
	public ProductResDto createProduct(ProductReqDto productReqDto) {

		// 특정 업체 존재 유무 확인
		// feign client
		UUID hubId = companyClient.getHubIdIfCompanyExists(productReqDto.getSupplierId());

		if (hubId == null)
			throw new EntityNotFoundException("Supplier Fot Found By Id : " + productReqDto.getSupplierId());
		// 업체가 존재하면 그 업체가 소속한 허브 id 등록

		return productRepository.save(Product.builder()
			.name(productReqDto.getName())
			.description(productReqDto.getDescription())
			.price(productReqDto.getPrice())
			.quantity(productReqDto.getQuantity())
			.supplierId(productReqDto.getSupplierId())
			.hubId(hubId)
			.status(ProductStatus.AVAILABLE)
			.build()).toResponseDto();
	}

	@Cacheable(cacheNames = "productCache", key = "#id")
	@Transactional(readOnly = true)
	public ProductResDto getProductById(UUID id) {
		return productRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new ProductNotFoundException("Product Not Found By Id : " + id))
			.toResponseDto();
	}

	@Cacheable(cacheNames = "productSearchCache", key = "#q + '-' + #category + '-' + #page + '-' + #size")
	public Page<ProductResDto> searchProducts(String q, String category, int page, int size, String sort,
		String order) {

		Page<Product> productPages = productRepository.findAll(q, category, page, size, sort, order);

		return productPages.map(Product::toResponseDto);
	}

	@CachePut(cacheNames = "productCache", key = "#id")
	@Caching(evict = {
		@CacheEvict(cacheNames = "productSearchCache", allEntries = true)
	})
	public ProductResDto updateProduct(UUID id, ProductReqDto productReqDto) {
		Product targetProduct = productRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new ProductNotFoundException("Product Not Found By Id :" + id));

		return productRepository.save(targetProduct.toBuilder()
			.name(productReqDto.getName())
			.description(productReqDto.getDescription())
			.price(productReqDto.getPrice())
			.quantity(productReqDto.getQuantity())
			.supplierId(productReqDto.getSupplierId())
			.hubId(productReqDto.getHubId())
			.updatedAt(LocalDateTime.now())
			.updatedBy("사용자")
			.build()).toResponseDto();
	}

	@Caching(evict = {
		@CacheEvict(cacheNames = "productCache", key = "#id"),
		@CacheEvict(cacheNames = "productSearchCache", key = "#id")
	})
	public void deleteProduct(UUID id) {
		Product targetProduct = productRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new ProductNotFoundException("Product Not Found By Id :" + id));

		productRepository.save(targetProduct.toBuilder()
			.deletedAt(LocalDateTime.now())
			.deletedBy("사용자")
			.build());
	}

	public void decreaseStock(StockDecrementMessage stockDecrementMessage) {
		UUID targetProductId = stockDecrementMessage.getProductId();
		int orderQuantity = stockDecrementMessage.getQuantity();

		// 1. 재고 차감 시도 (쿼리로 처리)
		int updatedRow = productRepository.decreaseStock(targetProductId, orderQuantity);

		// 2. 재고 부족 처리
		if (updatedRow == 0) {
			StockDecrementMessage errorMessage = stockDecrementMessage.toBuilder()
				.status("OUT_OF_STOCK")
				.build();
			rabbitTemplate.convertAndSend(queueResponseStock, errorMessage);
			return;
		}

		// 3. 정상 처리 메시지 발송
		Product updatedProduct = productRepository.findByIdAndDeletedAtIsNull(targetProductId)
			.orElseThrow(() -> new ProductNotFoundException("Product Not Found By Id :" + targetProductId));

		if (updatedProduct.getQuantity() == 0) {
			updatedProduct.statusSoldOut();
			productRepository.save(updatedProduct);
			// Sold out 메시지 전송
		}

		StockDecrementMessage successMessage = stockDecrementMessage.toBuilder()
			.status("SUCCESS")
			.supplierId(updatedProduct.getSupplierId())
			.price(updatedProduct.getPrice())
			.build();

		rabbitTemplate.convertAndSend(queueResponseStock, successMessage);
	}

	public void recoveryStock(StockReversalMessage stockReversalMessage) {

		UUID productId = stockReversalMessage.getProductId();
		int recoveryQuantity = stockReversalMessage.getQuantity();

		Product recoveryProduct = productRepository.findByIdAndDeletedAtIsNull(productId)
			.orElseThrow(
				() -> new ProductNotFoundException("Product Not Found By Id :" + productId));

		productRepository.save(recoveryProduct.toBuilder()
			.quantity(recoveryProduct.getQuantity() + recoveryQuantity)
			.build());
	}
}
