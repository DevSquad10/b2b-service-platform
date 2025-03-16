package com.devsquad10.shipping.domain.repository;

import com.devsquad10.shipping.domain.model.ShippingHistory;

public interface ShippingHistoryRepository {

	ShippingHistory save(ShippingHistory shippingHistory);
}
