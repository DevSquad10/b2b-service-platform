package com.devsquad10.hub.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.devsquad10.hub.domain.model.Hub;

public interface HubRepository {
	Hub save(Hub hub);

	Optional<Hub> findById(UUID id);
}
