package com.devsquad10.hub.application.service;

import org.springframework.stereotype.Service;

import com.devsquad10.hub.application.dto.RouteCalculationResult;
import com.devsquad10.hub.domain.model.Hub;
import com.devsquad10.hub.domain.repository.HubConnectionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubToHubRelayCalculateStrategy implements HubRouteCalculateStrategy {

	private final HubConnectionRepository hubConnectionRepository;

	@Override
	public RouteCalculationResult calculateRouteWithApi(Hub departureHub, Hub destinationHub) {
		return null;
	}

	@Override
	public RouteCalculationResult calculateRoute(Hub departureHub, Hub destinationHub) {
		return null;
	}
}
