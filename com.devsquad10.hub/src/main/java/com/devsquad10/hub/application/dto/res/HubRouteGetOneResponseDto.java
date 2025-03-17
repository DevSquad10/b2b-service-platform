package com.devsquad10.hub.application.dto.res;

import java.time.LocalDateTime;
import java.util.UUID;

import com.devsquad10.hub.domain.model.HubRoute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HubRouteGetOneResponseDto {
	private UUID id;
	private UUID departureHubId;
	private String departureHubName;
	private UUID destinationHubId;
	private String destinationHubName;
	private Double distance;
	private Integer duration;
	private LocalDateTime createdAt;
	private UUID createdBy;
	private LocalDateTime updatedAt;
	private UUID updatedBy;

	// TODO: 경유지 추후 구현
	// private List<UUID> waypoints;

	public static HubRouteGetOneResponseDto toResponseDto(HubRoute hubRoute) {
		return HubRouteGetOneResponseDto.builder()
			.id(hubRoute.getId())
			.departureHubId(hubRoute.getDepartureHub().getId())
			.departureHubName(hubRoute.getDepartureHub().getName())
			.destinationHubId(hubRoute.getDestinationHub().getId())
			.destinationHubName(hubRoute.getDestinationHub().getName())
			.distance(hubRoute.getDistance())
			.duration(hubRoute.getDuration())
			.createdAt(hubRoute.getCreatedAt())
			.createdBy(hubRoute.getCreatedBy())
			.updatedAt(hubRoute.getUpdatedAt())
			.updatedBy(hubRoute.getUpdatedBy())
			.build();
	}
}
