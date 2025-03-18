package com.devsquad10.hub.application.service;

import com.devsquad10.hub.application.dto.RouteCalculationResult;
import com.devsquad10.hub.domain.model.Hub;

public interface HubRouteCalculateService {
	RouteCalculationResult calculateRoute(Hub departureHub, Hub destinationHub);
}
