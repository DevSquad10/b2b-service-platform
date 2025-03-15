package com.devsquad10.product.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsquad10.product.application.dto.ProductReqDto;
import com.devsquad10.product.application.dto.ProductResponse;
import com.devsquad10.product.application.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

	private final ProductService productService;

	@PostMapping
	public ResponseEntity<?> createProduct(@RequestBody ProductReqDto productReqDto) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(ProductResponse.success(HttpStatus.OK.value(), productService.createProduct(productReqDto)));

	}
}
