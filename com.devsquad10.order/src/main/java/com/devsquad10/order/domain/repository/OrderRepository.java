package com.devsquad10.order.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.devsquad10.order.domain.model.Order;

public interface OrderRepository {

	Optional<Order> findByIdAndDeletedAtIsNull(UUID id);

	Order save(Order order);

	Page<Order> findAll(String q, String category, int page, int size, String sort, String order);
}
