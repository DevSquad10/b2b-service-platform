package com.devsquad10.hub.application.service;

import org.springframework.stereotype.Service;

import com.devsquad10.hub.application.dto.RouteCalculationResult;
import com.devsquad10.hub.domain.model.Hub;

@Service
public class P2PRouteCalculateService implements HubRouteCalculateService {
	@Override
	public RouteCalculationResult calculateRoute(Hub departureHub, Hub destinationHub) {
		return null;
	}
}
