package com.devsquad10.order.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.devsquad10.order.domain.model.Order;

public interface OrderRepository {

	Optional<Order> findByIdAndDeletedAtIsNull(UUID id);

	Order save(Order order);
}
