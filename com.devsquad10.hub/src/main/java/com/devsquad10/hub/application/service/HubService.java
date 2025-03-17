package com.devsquad10.hub.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsquad10.hub.application.exception.HubNotFoundException;
import com.devsquad10.hub.domain.model.Hub;
import com.devsquad10.hub.domain.repository.HubRepository;
import com.devsquad10.hub.presentation.req.HubCreateRequestDto;
import com.devsquad10.hub.presentation.req.HubSearchRequestDto;
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

	@Caching(evict = {
		@CacheEvict(value = "hubSearchCache", allEntries = true)
	})
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

	@Cacheable(value = "hubCache", key = "#id.toString()")
	public HubGetOneResponseDto getOneHub(UUID id) {
		Hub hub = hubRepository.findById(id)
			.orElseThrow(() -> new HubNotFoundException("Hub not found with id: " + id.toString()));

		return HubGetOneResponseDto.toResponseDto(hub);
	}

	@Caching(
		put = {@CachePut(value = "hubCache", key = "#id.toString()")},
		evict = {@CacheEvict(value = "hubSearchCache", allEntries = true)}
	)
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

	@Caching(evict = {
		@CacheEvict(value = "hubCache", key = "#id.toString()"),
		@CacheEvict(value = "hubSearchCache", allEntries = true)
	})
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

	@Cacheable(value = "hubSearchCache",
		key = "{#request.id, #request.name, #request.address, #request.page, #request.size, #request.sortOption?.name(), #request.sortOrder?.name()}"
	)
	public PagedHubResponseDto getHub(HubSearchRequestDto request) {

		Page<Hub> hubPage = hubRepository.findAll(request);

		Page<PagedHubItemResponseDto> dtoPage = hubPage.map(PagedHubItemResponseDto::toResponseDto);

		return PagedHubResponseDto.toResponseDto(dtoPage, request.getSortOption());
	}
}
