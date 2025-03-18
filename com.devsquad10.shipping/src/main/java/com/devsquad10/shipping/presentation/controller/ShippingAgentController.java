package com.devsquad10.shipping.presentation.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsquad10.shipping.application.dto.ShippingAgentResponse;
import com.devsquad10.shipping.application.dto.response.ShippingAgentResDto;
import com.devsquad10.shipping.application.service.ShippingAgentService;
import com.devsquad10.shipping.infrastructure.client.ShippingAgentPostFeignRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shipping-agent")
public class ShippingAgentController {

	private final ShippingAgentService shippingAgentService;

	//TODO: 유저 feign client 호출하면 배송관리자 생성 endpoint 로 연결
	// 권한 확인 - MASTER, 담당 HUB
	@PostMapping
	public void createShippingAgent(@Valid @RequestBody ShippingAgentPostFeignRequest request) {
		shippingAgentService.createShippingAgent(request);
	}

	// TODO: 권한 확인 - MASTER, 담당 HUB, 담당 DLV_AGENT
	@GetMapping("/{id}")
	public ResponseEntity<ShippingAgentResponse<ShippingAgentResDto>> getShippingAgent(
		@PathVariable(name = "id") UUID id) {

		return ResponseEntity.ok(ShippingAgentResponse.success(
				HttpStatus.OK.value(),
				shippingAgentService.getShippingAgentById(id))
			);
	}




}
