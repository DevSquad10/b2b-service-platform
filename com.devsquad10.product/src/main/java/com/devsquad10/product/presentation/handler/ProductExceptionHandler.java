package com.devsquad10.product.presentation.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.devsquad10.product.application.dto.ProductResponse;
import com.devsquad10.product.application.exception.ProductNotFoundException;

@RestControllerAdvice
public class ProductExceptionHandler {

	@ExceptionHandler(ProductNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ProductResponse<String>> productNotFoundException(ProductNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(ProductResponse.failure(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ProductResponse<String>> exception(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ProductResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
	}
}
