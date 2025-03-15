package com.devsquad10.product.application.service;

import org.springframework.stereotype.Service;

import com.devsquad10.product.application.dto.ProductReqDto;
import com.devsquad10.product.application.dto.ProductResDto;
import com.devsquad10.product.domain.model.Product;
import com.devsquad10.product.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;

	public ProductResDto createProduct(ProductReqDto productReqDto) {

		// 특정 업체 존재 유무 확인

		// 허브 소속 확인

		return productRepository.save(Product.builder()
			.name(productReqDto.getName())
			.description(productReqDto.getDescription())
			.price(productReqDto.getPrice())
			.quantity(productReqDto.getQuantity())
			.supplierId(productReqDto.getSupplierId())
			.hubId(productReqDto.getHubId())
			.build()).toResponseDto();
	}
}
