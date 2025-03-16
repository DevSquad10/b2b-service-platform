package com.devsquad10.hub.infrastructure.repository;

import org.springframework.data.domain.Page;

import com.devsquad10.hub.domain.model.Hub;
import com.devsquad10.hub.presentation.req.HubSearchRequestDto;

public interface HubRepositoryCustom {
	Page<Hub> findAll(HubSearchRequestDto request);
}
