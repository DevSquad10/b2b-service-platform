package com.devsquad10.hub.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.devsquad10.hub.application.dto.req.HubSearchRequestDto;
import com.devsquad10.hub.domain.model.Hub;

public interface HubRepository {
	Hub save(Hub hub);

	Optional<Hub> findById(UUID id);

	Page<Hub> findAll(HubSearchRequestDto request);
}
