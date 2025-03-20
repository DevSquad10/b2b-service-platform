package com.devsquad10.shipping.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devsquad10.shipping.domain.model.ShippingAgent;

import jakarta.persistence.LockModeType;

public interface ShippingAgentRepository {
	ShippingAgent save(ShippingAgent shippingAgent);

	Optional<Integer> findMaxShippingSequence();

	// 배송담당자 기본키 조회
	Optional<ShippingAgent> findByIdAndDeletedAtIsNull(UUID id);

	// 배송담당자 사용자ID 조회
	Optional<ShippingAgent> findByShippingManagerIdAndDeletedAtIsNull(UUID shippingManagerId);

	// 비관적 락(배타적 락) : 배송담당자 배정할 때, 사용
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select sa from ShippingAgent sa where sa.id = :id and sa.deletedAt is null")
	ShippingAgent findByIdWithPessimisticLock(@Param("id") UUID id);

	List<ShippingAgent> findAllByDeletedAtIsNull();

	// Page<ShippingAgent> findAll(String query, String category, Pageable pageable);
}
