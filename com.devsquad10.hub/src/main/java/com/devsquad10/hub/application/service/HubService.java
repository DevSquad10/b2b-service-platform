package com.devsquad10.hub.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsquad10.hub.application.exception.HubNotFoundException;
import com.devsquad10.hub.domain.model.Hub;
import com.devsquad10.hub.domain.repository.HubRepository;
import com.devsquad10.hub.presentation.req.HubCreateRequestDto;
import com.devsquad10.hub.presentation.req.HubGetRequestDto;
import com.devsquad10.hub.presentation.req.HubUpdateRequestDto;
import com.devsquad10.hub.presentation.res.HubCreateResponseDto;
import com.devsquad10.hub.presentation.res.HubDeleteResponseDto;
import com.devsquad10.hub.presentation.res.HubGetOneResponseDto;
import com.devsquad10.hub.presentation.res.HubUpdateResponseDto;
import com.devsquad10.hub.presentation.res.PagedHubItemResponseDto;
import com.devsquad10.hub.presentation.res.PagedHubResponseDto;

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

	public HubGetOneResponseDto getOneHub(UUID id) {
		Hub hub = hubRepository.findById(id)
			.orElseThrow(() -> new HubNotFoundException("Hub not found with id: " + id.toString()));

		return HubGetOneResponseDto.toResponseDto(hub);
	}

	public HubUpdateResponseDto updateHub(UUID id, HubUpdateRequestDto request) {
		Hub hub = hubRepository.findById(id)
			.orElseThrow(() -> new HubNotFoundException("Hub not found with id: " + id.toString()));

		hub.update(
			request.getName(),
			request.getAddress(),
			request.getLatitude(),
			request.getLongitude()
		);

		Hub updatedHub = hubRepository.save(hub);
		return HubUpdateResponseDto.toResponseDto(updatedHub);
	}

	public HubDeleteResponseDto deleteHub(UUID id) {
		Hub hub = hubRepository.findById(id)
			.orElseThrow(() -> new HubNotFoundException("Hub not found with id: " + id.toString()));

		// TODO: deleted_by 구현 시 수정
		hub.delete(UUID.randomUUID());
		hubRepository.save(hub);

		return HubDeleteResponseDto.builder()
			.deletedAt(LocalDateTime.now())
			.build();
	}

	public PagedHubResponseDto getHub(HubGetRequestDto request) {
		// TODO: 정렬 조건 추후 구현
		Pageable pageable = PageRequest.of(
			request.getPage(),
			request.getSize()
		);

		Page<Hub> hubPage = hubRepository.findAll(pageable);

		Page<PagedHubItemResponseDto> dtoPage = hubPage.map(PagedHubItemResponseDto::toResponseDto);

		return PagedHubResponseDto.toResponseDto(dtoPage, request.getSortOption());
	}
}
