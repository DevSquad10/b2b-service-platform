package com.devsquad10.hub.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsquad10.hub.domain.model.Hub;
import com.devsquad10.hub.domain.repository.HubRepository;
import com.devsquad10.hub.presentation.req.HubCreateRequestDto;
import com.devsquad10.hub.presentation.res.HubCreateResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class HubService {

	private final HubRepository hubRepository;

	public HubCreateResponseDto createHub(HubCreateRequestDto request) {
		Hub hub = Hub.builder()
			.name(request.getName())
			.address(request.getAddress())
			.latitude(request.getLatitude())
			.longitude(request.getLongitude())
			.build();

		Hub savedHub = hubRepository.save(hub);

		return HubCreateResponseDto.toResponseDto(savedHub);
	}
}
