package com.devsquad10.shipping.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.devsquad10.shipping.domain.model.Shipping;

public interface ShippingRepository {

	Shipping save(Shipping shipping);

	Optional<Shipping> findByIdAndDeletedAtIsNull(UUID id);
}
