package com.devsquad10.hub.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.devsquad10.hub.domain.model.HubRoute;

public interface HubRouteRepository {
	HubRoute save(HubRoute hubRoute);

	Optional<HubRoute> findById(UUID id);
}
