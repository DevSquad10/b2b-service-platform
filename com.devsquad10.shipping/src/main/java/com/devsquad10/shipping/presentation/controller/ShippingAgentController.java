package com.devsquad10.shipping.presentation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsquad10.shipping.application.service.ShippingAgentService;
import com.devsquad10.shipping.infrastructure.client.ShippingAgentPostFeignRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shipping-agent")
public class ShippingAgentController {

	private final ShippingAgentService shippingAgentService;

	// TODO: 유저 feign client 호출하면 배송관리자 생성 endpoint 로 연결
	@PostMapping
	public void createShippingAgent(@Valid @RequestBody ShippingAgentPostFeignRequest request) {
		shippingAgentService.createShippingAgent(request);
	}
}
