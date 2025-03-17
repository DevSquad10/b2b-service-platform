package com.devsquad10.hub.domain.repository;

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
}
