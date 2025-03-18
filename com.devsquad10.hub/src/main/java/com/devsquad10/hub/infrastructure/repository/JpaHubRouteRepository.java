package com.devsquad10.hub.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsquad10.hub.domain.model.HubRoute;

@Repository
public interface JpaHubRouteRepository extends JpaRepository<HubRoute, UUID>, HubRouteRepositoryCustom {
}
