package com.devsquad10.shipping.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.devsquad10.shipping.domain.model.ShippingAgent;

public interface ShippingAgentRepository {
	ShippingAgent save(ShippingAgent shippingAgent);

	Optional<Integer> findMaxShippingSequence();

	// 배송담당자 기본키 조회
	Optional<ShippingAgent> findByIdAndDeletedAtIsNull(UUID id);

	// 배송담당자 사용자ID 조회
	Optional<ShippingAgent> findByShippingManagerIdAndDeletedAtIsNull(UUID shippingManagerId);

	// Page<ShippingAgent> findAll(String query, String category, Pageable pageable);
}
