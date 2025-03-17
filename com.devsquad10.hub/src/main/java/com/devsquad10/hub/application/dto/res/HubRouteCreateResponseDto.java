package com.devsquad10.hub.application.dto.res;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.devsquad10.hub.domain.model.HubRoute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HubRouteCreateResponseDto {
	private UUID id;
	private UUID departureHubId;
	private String departureHubName;
	private UUID destinationHubId;
	private String destinationHubName;
	private Double distance;
	private Integer duration;
	private LocalDateTime createdAt;
	private UUID createdBy;
	private List<UUID> waypoints;

	public static HubRouteCreateResponseDto toResponseDto(HubRoute hubRoute, List<UUID> waypoints) {
		return HubRouteCreateResponseDto.builder()
			.id(hubRoute.getId())
			.departureHubId(hubRoute.getDepartureHub().getId())
			.departureHubName(hubRoute.getDepartureHub().getName())
			.destinationHubId(hubRoute.getDestinationHub().getId())
			.destinationHubName(hubRoute.getDestinationHub().getName())
			.distance(hubRoute.getDistance())
			.duration(hubRoute.getDuration())
			.createdAt(hubRoute.getCreatedAt())
			.createdBy(hubRoute.getCreatedBy())
			.waypoints(waypoints)
			.build();
	}
}
