package com.devsquad10.hub.domain.repository;

import org.springframework.stereotype.Repository;

import com.devsquad10.hub.domain.model.Hub;
import com.devsquad10.hub.infrastructure.repository.JpaHubRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HubRepositoryImpl implements HubRepository {

	private final JpaHubRepository jpaHubRepository;

	@Override
	public Hub save(Hub hub) {
		return jpaHubRepository.save(hub);
	}
}
