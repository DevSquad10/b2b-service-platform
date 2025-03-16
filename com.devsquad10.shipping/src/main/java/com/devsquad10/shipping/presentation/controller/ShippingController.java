package com.devsquad10.shipping.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsquad10.shipping.application.dto.ShippingPostReqDto;
import com.devsquad10.shipping.application.service.ShippingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shipping")
public class ShippingController {

	private final ShippingService shippingService;

	@PostMapping
	public ResponseEntity<String> shipping(@RequestBody ShippingPostReqDto shippingReqDto) {

		shippingService.createShipping(shippingReqDto);

		return ResponseEntity.status(HttpStatus.OK).body("배송 생성");
	}
}
