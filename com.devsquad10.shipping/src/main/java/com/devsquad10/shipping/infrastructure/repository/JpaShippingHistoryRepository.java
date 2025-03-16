package com.devsquad10.shipping.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsquad10.shipping.domain.model.ShippingHistory;
import com.devsquad10.shipping.domain.repository.ShippingHistoryRepository;

@Repository
public interface JpaShippingHistoryRepository extends JpaRepository<ShippingHistory, UUID>, ShippingHistoryRepository {
}
