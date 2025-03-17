package com.devsquad10.company.presentation.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.devsquad10.company.application.dto.CompanyResponse;

@RestControllerAdvice
public class CompanyExceptionHandler {

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<CompanyResponse<String>> CompanyNotFoundException(Exception ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(CompanyResponse.failure(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<CompanyResponse<String>> exception(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(CompanyResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
	}
}
