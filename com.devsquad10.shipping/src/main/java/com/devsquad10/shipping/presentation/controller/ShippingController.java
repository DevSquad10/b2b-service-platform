package com.devsquad10.shipping.presentation.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	public ResponseEntity<ShippingResponse<?>> shipping(@RequestBody ShippingPostReqDto shippingReqDto) {
		try {
			return ResponseEntity.status(HttpStatus.OK)
				.body(ShippingResponse.success(HttpStatus.OK.value(), shippingService.createShipping(shippingReqDto)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ShippingResponse.failure(HttpStatus.BAD_REQUEST.value(), "배송 생성 불가능 : " + e.getMessage()));
		}
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
				.body(ShippingResponse.failure(HttpStatus.BAD_REQUEST.value(), "배송 수정 불가능: " + e.getMessage()));
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<ShippingResponse<?>> getShippingById(@PathVariable(name = "id") UUID id) {
		try {
			return ResponseEntity.ok(ShippingResponse.success(HttpStatus.OK.value(), shippingService.getShippingById(id)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ShippingResponse.failure(HttpStatus.BAD_REQUEST.value(), "배송 조회 불가능: " + e.getMessage()));
		}
	}

	@GetMapping("/search")
	public ResponseEntity<ShippingResponse<?>> searchShipping(
		@RequestParam(name = "query", required = false) String query,
		@RequestParam(name = "category", required = false) String category,
		@RequestParam(name = "page",defaultValue= "0") int page,
		@RequestParam(name = "size",defaultValue = "10") int size,
		@RequestParam(name = "sortBy", defaultValue = "createdAt") String sort,
		@RequestParam(name = "order", defaultValue = "desc") String order) {

		try {
			return ResponseEntity.status(HttpStatus.OK)
				.body(ShippingResponse.success(HttpStatus.OK.value(),
					shippingService.searchShipping(query, category, page, size, sort, order)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ShippingResponse.failure(HttpStatus.BAD_REQUEST.value(), "검색 결과가 존재하지 않음 : " + e.getMessage()));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ShippingResponse<String>> deleteShipping(@PathVariable(name = "id") UUID id) {

		try {
			shippingService.deleteShipping(id);

			return ResponseEntity.status(HttpStatus.OK)
				.body(ShippingResponse.success(HttpStatus.OK.value(), "배송이 삭제되었습니다."));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ShippingResponse.failure(HttpStatus.BAD_REQUEST.value(), "배송 삭제 불가능 : " + e.getMessage()));
		}
	}
}
