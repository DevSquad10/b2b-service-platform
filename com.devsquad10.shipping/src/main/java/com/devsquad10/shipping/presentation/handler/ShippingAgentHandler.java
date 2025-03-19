package com.devsquad10.shipping.presentation.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.devsquad10.shipping.application.dto.ShippingAgentResponse;
import com.devsquad10.shipping.application.exception.shippingAgent.HubIdNotFoundException;
import com.devsquad10.shipping.application.exception.shippingAgent.ShippingAgentNotFoundException;
import com.devsquad10.shipping.application.exception.shippingAgent.ShippingAgentTypeNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ShippingAgentHandler {

	// 배송 담당자 - 커스텀 예외 처리
	// 담당자 타입 존재X
	@ExceptionHandler(ShippingAgentNotFoundException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ShippingAgentResponse<String>> handlerShippingAgentNotFoundException(ShippingAgentNotFoundException e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ShippingAgentResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
	}

	// 담당자 타입 존재X
	@ExceptionHandler(ShippingAgentTypeNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ShippingAgentResponse<String>> handlerShippingAgentTypeNotFoundException(ShippingAgentTypeNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(ShippingAgentResponse.failure(HttpStatus.NOT_FOUND.value(), e.getMessage()));
	}

	// 허브Id 존재X
	@ExceptionHandler(HubIdNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ShippingAgentResponse<String>> handlerHubIdNotFoundException(HubIdNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(ShippingAgentResponse.failure(HttpStatus.NOT_FOUND.value(), e.getMessage()));
	}

	// 허브 응답 받은 json null
	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ShippingAgentResponse<String>> handlerEntityNotFoundException(EntityNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(ShippingAgentResponse.failure(HttpStatus.NOT_FOUND.value(), e.getMessage()));
	}
}