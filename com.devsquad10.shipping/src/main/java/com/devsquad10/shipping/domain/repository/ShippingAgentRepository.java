package com.devsquad10.shipping.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.devsquad10.shipping.domain.model.ShippingAgent;

public interface ShippingAgentRepository {
	ShippingAgent save(ShippingAgent shippingAgent);

	Optional<Integer> findMaxShippingSequence();

	Optional<ShippingAgent> findById(UUID id);
}
