package com.devsquad10.shipping.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.devsquad10.shipping.domain.model.Shipping;

public interface ShippingRepository {

	Shipping save(Shipping shipping);

	Optional<Shipping> findByIdAndDeletedAtIsNull(UUID id);

	Page<Shipping> findAll(String q, String category, int page, int size, String sort, String order);
}
