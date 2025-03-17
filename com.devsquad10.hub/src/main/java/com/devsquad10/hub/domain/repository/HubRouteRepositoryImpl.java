package com.devsquad10.hub.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.devsquad10.hub.domain.model.HubRoute;
import com.devsquad10.hub.infrastructure.repository.JpaHubRouteRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HubRouteRepositoryImpl implements HubRouteRepository {

	private final JpaHubRouteRepository jpaHubRouteRepository;

	@Override
	public HubRoute save(HubRoute hubRoute) {
		return jpaHubRouteRepository.save(hubRoute);
	}

	@Override
	public Optional<HubRoute> findById(UUID id) {
		return jpaHubRouteRepository.findById(id);
	}
}
