package com.devsquad10.shipping.presentation.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsquad10.shipping.application.dto.ShippingPostReqDto;
import com.devsquad10.shipping.application.dto.ShippingResponse;
import com.devsquad10.shipping.application.dto.ShippingUpdateReqDto;
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

	@PatchMapping("/{id}")
	public ResponseEntity<ShippingResponse<?>> updateShipping(@PathVariable(name = "id") UUID id,
												@RequestBody ShippingUpdateReqDto shippingUpdateReqDto) {
		try {
			if(shippingUpdateReqDto.getStatus() != null) {
				return ResponseEntity.status(HttpStatus.OK)
					.body(ShippingResponse.success(HttpStatus.OK.value(), shippingService.statusUpdateShipping(id, shippingUpdateReqDto)));
			} else if(shippingUpdateReqDto.getCompanyShippingManagerId() != null) {
				return ResponseEntity.status(HttpStatus.OK)
					.body(ShippingResponse.success(HttpStatus.OK.value(), shippingService.managerIdUpdateShipping(id, shippingUpdateReqDto)));
			} else if(shippingUpdateReqDto.getOrderId() != null) {
				return ResponseEntity.status(HttpStatus.OK)
					.body(ShippingResponse.success(HttpStatus.OK.value(), shippingService.orderIdUpdateShipping(id, shippingUpdateReqDto)));
			} else if(shippingUpdateReqDto.getAddress() != null || shippingUpdateReqDto.getRequestDetails() != null) {
				return ResponseEntity.status(HttpStatus.OK)
					.body(ShippingResponse.success(HttpStatus.OK.value(), shippingService.infoUpdateShipping(id, shippingUpdateReqDto)));
			} else {
				return ResponseEntity.status(HttpStatus.OK)
					.body(ShippingResponse.success(HttpStatus.OK.value(), shippingService.updateShipping(id, shippingUpdateReqDto)));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK)
				.body(ShippingResponse.failure(HttpStatus.BAD_REQUEST.value(), "배송 수정이 불가능합니다."));
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<ShippingResponse<?>> getShippingById(@PathVariable(name = "id") UUID id) {
		try {
			return ResponseEntity.ok(ShippingResponse.success(HttpStatus.OK.value(), shippingService.getShippingById(id)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ShippingResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
		}
	}
}
