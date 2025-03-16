package com.devsquad10.hub.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsquad10.hub.application.service.HubService;
import com.devsquad10.hub.presentation.req.HubCreateRequestDto;
import com.devsquad10.hub.presentation.res.ApiResponse;
import com.devsquad10.hub.presentation.res.HubCreateResponseDto;

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
}
