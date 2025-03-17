package com.devsquad10.hub.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsquad10.hub.application.dto.req.HubRouteCreateRequestDto;
import com.devsquad10.hub.application.dto.res.ApiResponse;
import com.devsquad10.hub.application.dto.res.HubRouteCreateResponseDto;
import com.devsquad10.hub.application.service.HubRouteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hub-route")
public class HubRouteController {

	private final HubRouteService hubRouteService;

	@PostMapping
	public ResponseEntity<ApiResponse<HubRouteCreateResponseDto>> createHubRoute(
		@Valid @RequestBody HubRouteCreateRequestDto request
	) {
		HubRouteCreateResponseDto response = hubRouteService.createHubRoute(request);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(
				HttpStatus.OK.value(),
				response
			));
	}
}
