package com.devsquad10.hub.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.devsquad10.hub.domain.model.Hub;
import com.devsquad10.hub.infrastructure.repository.JpaHubRepository;
import com.devsquad10.hub.presentation.req.HubSearchRequestDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HubRepositoryImpl implements HubRepository {

	private final JpaHubRepository jpaHubRepository;

	@Override
	public Hub save(Hub hub) {
		return jpaHubRepository.save(hub);
	}

	@Override
	public Optional<Hub> findById(UUID id) {
		return jpaHubRepository.findById(id);
	}

	@Override
	public Page<Hub> findAll(HubSearchRequestDto request) {
		return jpaHubRepository.findAll(request);
	}
}
