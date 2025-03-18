package com.devsquad10.shipping.domain.repository;

import java.util.Optional;

import com.devsquad10.shipping.domain.model.ShippingAgent;

public interface ShippingAgentRepository {
	ShippingAgent save(ShippingAgent shippingAgent);

	Optional<Integer> findMaxShippingSequence();
}
