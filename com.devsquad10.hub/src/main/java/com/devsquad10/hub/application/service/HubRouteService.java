package com.devsquad10.hub.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
	private final HubRouteCalculateService hubRouteCalculateService;

	public HubRouteCreateResponseDto createHubRoute(HubRouteCreateRequestDto request) {
		Hub departureHub = hubRepository.findById(request.getDepartureHubId())
			.orElseThrow(() -> new HubNotFoundException("출발 허브를 찾을 수 없습니다. ID: " + request.getDepartureHubId()));

		Hub destinationHub = hubRepository.findById(request.getDestinationHubId())
			.orElseThrow(() -> new HubNotFoundException("도착 허브를 찾을 수 없습니다. ID: " + request.getDestinationHubId()));

		RouteCalculationResult calculationResult =
			hubRouteCalculateService.calculateRoute(departureHub, destinationHub);

		HubRoute hubRoute = HubRoute.builder()
			.departureHub(departureHub)
			.destinationHub(destinationHub)
			// TODO: 임시 값 설정
			// .distance(calculationResult.getDistance())
			// .duration(calculationResult.getDuration())
			.distance(Math.random() * 10000)
			.duration((int)(Math.random() * 1000000))
			.build();

		HubRoute savedRoute = hubRouteRepository.save(hubRoute);

		// TODO: 임시 값 설정
		List<UUID> dummyList = new ArrayList<>();
		dummyList.add(UUID.fromString("11111111-1111-1111-1111-111111111101"));
		dummyList.add(UUID.fromString("11111111-1111-1111-1111-111111111102"));
		dummyList.add(UUID.fromString("11111111-1111-1111-1111-111111111103"));

		// return HubRouteCreateResponseDto.toResponseDto(savedRoute, calculationResult.getWaypoints());
		return HubRouteCreateResponseDto.toResponseDto(savedRoute, dummyList);
	}

	@Transactional(readOnly = true)
	public HubRouteGetOneResponseDto getOneHubRoute(UUID id) {
		HubRoute hubRoute = hubRouteRepository.findById(id)
			.orElseThrow(() -> new HubRouteNotFoundException("허브 경로를 찾을 수 없습니다."));

		return HubRouteGetOneResponseDto.toResponseDto(hubRoute);
	}

	public HubRouteUpdateResponseDto updateHubRoute(UUID id, HubRouteUpdateRequestDto request) {
		HubRoute hubRoute = hubRouteRepository.findById(id)
			.orElseThrow(() -> new HubRouteNotFoundException("허브 경로를 찾을 수 없습니다."));

		RouteCalculationResult calculationResult =
			hubRouteCalculateService.calculateRoute(hubRoute.getDepartureHub(), hubRoute.getDestinationHub());

		hubRoute.update(
			// TODO: 임시 값 설정
			// calculationResult.getDistance(),
			// calculationResult.getDuration()
			(Math.random() * 10000),
			((int)(Math.random() * 1000000))
		);

		// TODO: 임시 값 설정
		List<UUID> dummyList = new ArrayList<>();
		dummyList.add(UUID.fromString("11111111-1111-1111-1111-111111111101"));
		dummyList.add(UUID.fromString("11111111-1111-1111-1111-111111111102"));
		dummyList.add(UUID.fromString("11111111-1111-1111-1111-111111111103"));

		HubRoute updatedRoute = hubRouteRepository.save(hubRoute);

		// return HubRouteUpdateResponseDto.toResponseDto(updatedRoute, calculationResult.getWaypoints());
		return HubRouteUpdateResponseDto.toResponseDto(updatedRoute, dummyList);
	}

	public void deleteHubRoute(UUID id) {
		HubRoute hubRoute = hubRouteRepository.findById(id)
			.orElseThrow(() -> new HubRouteNotFoundException("허브 경로를 찾을 수 없습니다."));

		// TODO: 사용자 정보 구현 시 수정
		hubRoute.delete(UUID.randomUUID());
		hubRouteRepository.save(hubRoute);
	}

	public PagedHubRouteResponseDto getHub(HubRouteSearchRequestDto request) {
		Page<HubRoute> hubRoutePage = hubRouteRepository.findAll(request);

		Page<PagedHubRouteItemResponseDto> dtoPage = hubRoutePage.map(PagedHubRouteItemResponseDto::toResponseDto);

		return PagedHubRouteResponseDto.toResponseDto(dtoPage, request.getSortOption());
	}
}
