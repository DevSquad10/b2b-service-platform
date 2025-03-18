package com.devsquad10.order.presentation.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsquad10.order.application.dto.OrderReqDto;
import com.devsquad10.order.application.dto.OrderResDto;
import com.devsquad10.order.application.dto.response.OrderResponse;
import com.devsquad10.order.application.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	public ResponseEntity<OrderResponse<String>> createOrder(@RequestBody OrderReqDto orderReqDto) {

		orderService.createOrder(orderReqDto);

		return ResponseEntity.status(HttpStatus.OK)
			.body(OrderResponse.success(HttpStatus.OK.value(), "Order received successfully"));
	}

	@GetMapping("/{id}")
	public ResponseEntity<OrderResponse<OrderResDto>> getOrderById(@PathVariable("id") UUID id) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(OrderResponse.success(HttpStatus.OK.value(), orderService.getOrderById(id)));
	}
}
