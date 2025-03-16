package com.devsquad10.order.domain.repository;

import com.devsquad10.order.domain.model.Order;

public interface OrderRepository {

	Order save(Order order);
}
