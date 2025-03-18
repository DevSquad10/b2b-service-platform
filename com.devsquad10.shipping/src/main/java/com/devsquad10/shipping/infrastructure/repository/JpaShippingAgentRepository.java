package com.devsquad10.shipping.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsquad10.shipping.domain.model.ShippingAgent;

@Repository
public interface JpaShippingAgentRepository extends JpaRepository<ShippingAgent, UUID> {
}
