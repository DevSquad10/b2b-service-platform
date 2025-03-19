package com.devsquad10.hub.application.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsquad10.hub.application.dto.RouteCalculationResult;
import com.devsquad10.hub.application.dto.req.HubRouteCreateRequestDto;
import com.devsquad10.hub.application.dto.req.HubRouteSearchRequestDto;
import com.devsquad10.hub.application.dto.req.HubRouteUpdateRequestDto;
import com.devsquad10.hub.application.dto.res.HubRouteCreateResponseDto;
import com.devsquad10.hub.application.dto.res.HubRouteGetOneResponseDto;
import com.devsquad10.hub.application.dto.res.HubRouteUpdateResponseDto;
import com.devsquad10.hub.application.dto.res.PagedHubRouteItemResponseDto;
import com.devsquad10.hub.application.dto.res.PagedHubRouteResponseDto;
import com.devsquad10.hub.application.exception.HubNotFoundException;
import com.devsquad10.hub.application.exception.HubRouteNotFoundException;
import com.devsquad10.hub.domain.model.Hub;
import com.devsquad10.hub.domain.model.HubRoute;
import com.devsquad10.hub.domain.repository.HubRepository;
import com.devsquad10.hub.domain.repository.HubRouteRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class HubRouteService {

	private final HubRepository hubRepository;
	private final HubRouteRepository hubRouteRepository;
	private final HubRouteCalculateStrategy hubRouteCalculateStrategy;

	@Caching(evict = {
		@CacheEvict(value = "hubRouteSearchCache", allEntries = true)
	})
	public HubRouteCreateResponseDto createHubRoute(HubRouteCreateRequestDto request) {
		Hub departureHub = hubRepository.findById(request.getDepartureHubId())
			.orElseThrow(() -> new HubNotFoundException("출발 허브를 찾을 수 없습니다. ID: " + request.getDepartureHubId()));

		Hub destinationHub = hubRepository.findById(request.getDestinationHubId())
			.orElseThrow(() -> new HubNotFoundException("도착 허브를 찾을 수 없습니다. ID: " + request.getDestinationHubId()));

		Optional<HubRoute> existingRoute = hubRouteRepository.findByDepartureHubAndDestinationHub(
			departureHub, destinationHub);

		// TODO: 경유지 설정/이동 경로 전략 설정 후 변경
		if (existingRoute.isPresent()) {
			return HubRouteCreateResponseDto.toResponseDto(existingRoute.get());
		}
		RouteCalculationResult calculationResult =
			// hubRouteCalculateStrategy.calculateRoute(departureHub, destinationHub);
			hubRouteCalculateStrategy.calculateRouteWithApi(departureHub, destinationHub);

		HubRoute newHubRoute = HubRoute.builder()
			.departureHub(departureHub)
			.destinationHub(destinationHub)
			.distance(calculationResult.getDistance())
			.duration(calculationResult.getDuration())
			.build();

		HubRoute savedRoute = hubRouteRepository.save(newHubRoute);

		return HubRouteCreateResponseDto.toResponseDto(savedRoute);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "hubRouteCache", key = "#id.toString()")
	public HubRouteGetOneResponseDto getOneHubRoute(UUID id) {
		HubRoute hubRoute = hubRouteRepository.findById(id)
			.orElseThrow(() -> new HubRouteNotFoundException("허브 경로를 찾을 수 없습니다."));

		return HubRouteGetOneResponseDto.toResponseDto(hubRoute);
	}

	@Caching(
		put = {@CachePut(value = "hubRouteCache", key = "#id.toString()")},
		evict = {@CacheEvict(value = "hubRouteSearchCache", allEntries = true)}
	)
	public HubRouteUpdateResponseDto updateHubRoute(UUID id, HubRouteUpdateRequestDto request) {
		HubRoute hubRoute = hubRouteRepository.findById(id)
			.orElseThrow(() -> new HubRouteNotFoundException("허브 경로를 찾을 수 없습니다."));

		hubRoute.update(
			request.getDistance(),
			request.getDuration()
		);

		HubRoute updatedRoute = hubRouteRepository.save(hubRoute);

		return HubRouteUpdateResponseDto.toResponseDto(updatedRoute);
	}

	@Caching(evict = {
		@CacheEvict(value = "hubRouteCache", key = "#id.toString()"),
		@CacheEvict(value = "hubRouteSearchCache", allEntries = true)
	})
	public void deleteHubRoute(UUID id) {
		HubRoute hubRoute = hubRouteRepository.findById(id)
			.orElseThrow(() -> new HubRouteNotFoundException("허브 경로를 찾을 수 없습니다."));

		// TODO: 사용자 정보 구현 시 수정
		hubRoute.delete(UUID.randomUUID());
		hubRouteRepository.save(hubRoute);
	}

	@Cacheable(value = "hubRouteSearchCache",
		key = "#request.toString()"
	)
	public PagedHubRouteResponseDto getHub(HubRouteSearchRequestDto request) {
		Page<HubRoute> hubRoutePage = hubRouteRepository.findAll(request);

		Page<PagedHubRouteItemResponseDto> dtoPage = hubRoutePage.map(PagedHubRouteItemResponseDto::toResponseDto);

		return PagedHubRouteResponseDto.toResponseDto(dtoPage, request.getSortOption());
	}
}
