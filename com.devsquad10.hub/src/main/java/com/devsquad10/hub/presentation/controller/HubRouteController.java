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

import com.devsquad10.hub.application.dto.req.HubRouteCreateRequestDto;
import com.devsquad10.hub.application.dto.req.HubRouteUpdateRequestDto;
import com.devsquad10.hub.application.dto.res.ApiResponse;
import com.devsquad10.hub.application.dto.res.HubRouteCreateResponseDto;
import com.devsquad10.hub.application.dto.res.HubRouteGetOneResponseDto;
import com.devsquad10.hub.application.dto.res.HubRouteUpdateResponseDto;
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

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<HubRouteGetOneResponseDto>> getHubRoute(
		@PathVariable UUID id
	) {
		HubRouteGetOneResponseDto response = hubRouteService.getOneHubRoute(id);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(
				HttpStatus.OK.value(),
				response
			));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponse<HubRouteUpdateResponseDto>> updateHubRoute(
		@PathVariable UUID id,
		@Valid @RequestBody HubRouteUpdateRequestDto request
	) {
		HubRouteUpdateResponseDto response = hubRouteService.updateHubRoute(id, request);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(
				HttpStatus.OK.value(),
				response
			));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> deleteHubRoute(
		@PathVariable UUID id
	) {
		hubRouteService.deleteHubRoute(id);
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(
				HttpStatus.OK.value(),
				"HubRoute successfully deleted"
			));
	}
}
