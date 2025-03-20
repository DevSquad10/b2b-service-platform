package com.devsquad10.hub.application.dto;

import java.util.List;

import com.devsquad10.hub.domain.model.Hub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RouteCalculationResult {
	private Double distance;
	private Integer duration;

	// 경유 허브 리스트
	private List<Hub> waypoints;
}
