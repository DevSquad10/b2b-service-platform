package com.devsquad10.hub.application.service;

import com.devsquad10.hub.application.dto.RouteCalculationResult;
import com.devsquad10.hub.domain.model.Hub;

public class HubToHubRelayCalculateStrategy implements HubRouteCalculateStrategy {
	@Override
	public RouteCalculationResult calculateRouteWithApi(Hub departureHub, Hub destinationHub) {
		return null;
	}

	@Override
	public RouteCalculationResult calculateRoute(Hub departureHub, Hub destinationHub) {
		return null;
	}
}
