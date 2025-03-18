package com.devsquad10.shipping.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.devsquad10.shipping.domain.model.ShippingAgent;
import com.devsquad10.shipping.domain.repository.ShippingAgentRepository;

@Repository
public interface JpaShippingAgentRepository extends JpaRepository<ShippingAgent, UUID>, ShippingAgentRepository {
	// 순차적 순번 배정을 위한 최대값 추출
	@Query("SELECT MAX(a.shippingSequence) FROM ShippingAgent a")
	Optional<Integer> findMaxShippingSequence();
}
