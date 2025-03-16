package com.devsquad10.shipping.domain.repository;

import com.devsquad10.shipping.domain.model.Shipping;

public interface ShippingRepository {

	Shipping save(Shipping shipping);
}
