package com.devsquad10.hub.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsquad10.hub.application.dto.RouteCalculationResult;
import com.devsquad10.hub.application.dto.req.HubRouteCreateRequestDto;
import com.devsquad10.hub.application.dto.res.HubRouteCreateResponseDto;
import com.devsquad10.hub.application.exception.HubNotFoundException;
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
}
