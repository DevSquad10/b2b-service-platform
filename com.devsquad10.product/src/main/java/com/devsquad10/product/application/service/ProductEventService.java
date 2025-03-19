package com.devsquad10.product.application.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsquad10.product.application.dto.message.StockDecrementMessage;
import com.devsquad10.product.application.dto.message.StockReversalMessage;
import com.devsquad10.product.application.dto.message.StockSoldOutMessage;
import com.devsquad10.product.application.exception.ProductNotFoundException;
import com.devsquad10.product.domain.model.Product;
import com.devsquad10.product.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductEventService {

	@Value("${stockMessage.queue.stock.response}")
	private String queueResponseStock;

	@Value("${stockMessage.queue.stockSoldOut.request}")
	private String queueStockSoldOut;

	private final ProductRepository productRepository;
	private final RabbitTemplate rabbitTemplate;

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
		Product product = productRepository.findByIdAndDeletedAtIsNull(targetProductId)
			.orElseThrow(() -> new ProductNotFoundException("Product Not Found By Id :" + targetProductId));

		if (product.getQuantity() == 0) {
			product.statusSoldOut();
			productRepository.save(product);
			
			// company에 보내줘야할 것 ( 상품 id , 재고 소진 일자)
			StockSoldOutMessage stockSoldOutMessage = new StockSoldOutMessage(product.getId(), new Date());
			rabbitTemplate.convertAndSend(queueStockSoldOut, stockSoldOutMessage);
		}

		StockDecrementMessage successMessage = stockDecrementMessage.toBuilder()
			.status("SUCCESS")
			.supplierId(product.getSupplierId())
			.price(product.getPrice())
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
