package com.devsquad10.hub.application.dto.res;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.devsquad10.hub.domain.model.HubRoute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HubRouteUpdateResponseDto {
	private UUID id;
	private UUID departureHubId;
	private String departureHubName;
	private UUID destinationHubId;
	private String destinationHubName;
	private Double distance;
	private Integer duration;
	private LocalDateTime updatedAt;
	private UUID updatedBy;
	private List<UUID> waypoints;  // 경유지 정보

	public static HubRouteUpdateResponseDto toResponseDto(HubRoute hubRoute, List<UUID> waypoints) {
		return HubRouteUpdateResponseDto.builder()
			.id(hubRoute.getId())
			.departureHubId(hubRoute.getDepartureHub().getId())
			.departureHubName(hubRoute.getDepartureHub().getName())
			.destinationHubId(hubRoute.getDestinationHub().getId())
			.destinationHubName(hubRoute.getDestinationHub().getName())
			.distance(hubRoute.getDistance())
			.duration(hubRoute.getDuration())
			.updatedAt(hubRoute.getUpdatedAt())
			.updatedBy(hubRoute.getUpdatedBy())
			.waypoints(waypoints)
			.build();
	}
}
