package com.devsquad10.hub.presentation.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.devsquad10.hub.application.service.HubService;
import com.devsquad10.hub.presentation.req.HubCreateRequestDto;
import com.devsquad10.hub.presentation.req.HubUpdateRequestDto;
import com.devsquad10.hub.presentation.res.ApiResponse;
import com.devsquad10.hub.presentation.res.HubCreateResponseDto;
import com.devsquad10.hub.presentation.res.HubDeleteResponseDto;
import com.devsquad10.hub.presentation.res.HubGetOneResponseDto;
import com.devsquad10.hub.presentation.res.HubUpdateResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hub")
public class HubController {

	private final HubService hubService;

	@PostMapping
	public ResponseEntity<ApiResponse<HubCreateResponseDto>> createHub(
		@Valid @RequestBody HubCreateRequestDto request
	) {
		HubCreateResponseDto response = hubService.createHub(request);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(
				HttpStatus.OK.value(),
				response
			));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<HubGetOneResponseDto>> getHub(
		@PathVariable UUID id
	) {
		HubGetOneResponseDto response = hubService.getOneHub(id);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(
				HttpStatus.OK.value(),
				response
			));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponse<HubUpdateResponseDto>> updateHub(
		@PathVariable UUID id,
		@Valid @RequestBody HubUpdateRequestDto request
	) {
		HubUpdateResponseDto response = hubService.updateHub(id, request);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(
				HttpStatus.OK.value(),
				response
			));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<HubDeleteResponseDto>> deleteHub(
		@PathVariable UUID id
	) {
		HubDeleteResponseDto response = hubService.deleteHub(id);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(
				HttpStatus.OK.value(),
				response
			));
	}
}
