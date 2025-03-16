package com.devsquad10.hub.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.devsquad10.hub.domain.model.Hub;

public interface HubRepository {
	Hub save(Hub hub);

	Optional<Hub> findById(UUID id);

	Page<Hub> findAll(Pageable pageable);
}
