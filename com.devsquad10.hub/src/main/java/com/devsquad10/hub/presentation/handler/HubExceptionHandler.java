package com.devsquad10.hub.presentation.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.devsquad10.hub.application.dto.res.ApiResponse;
import com.devsquad10.hub.application.exception.HubNotFoundException;

@RestControllerAdvice
public class HubExceptionHandler {
	// TODO: custom error code로 설정
	@ExceptionHandler(HubNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ApiResponse<String>> handleHubNotFoundException(HubNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(ApiResponse.failure(HttpStatus.NOT_FOUND.value(), e.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ApiResponse<String>> handleHubUnexpectedException(Exception e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
	}
}
