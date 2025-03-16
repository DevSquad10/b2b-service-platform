package com.devsquad10.product.presentation.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsquad10.product.application.dto.ProductReqDto;
import com.devsquad10.product.application.dto.ProductResDto;
import com.devsquad10.product.application.dto.ProductResponse;
import com.devsquad10.product.application.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

	private final ProductService productService;

	@PostMapping
	public ResponseEntity<ProductResponse<ProductResDto>> createProduct(@RequestBody ProductReqDto productReqDto) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(ProductResponse.success(HttpStatus.OK.value(), productService.createProduct(productReqDto)));

	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse<ProductResDto>> getProductById(@PathVariable("id") UUID id) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(ProductResponse.success(HttpStatus.OK.value(), productService.getProductById(id)));
	}
}
