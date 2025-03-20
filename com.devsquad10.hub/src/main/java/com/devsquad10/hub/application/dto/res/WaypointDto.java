package com.devsquad10.hub.application.dto.res;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WaypointDto {
	private UUID hubId;
	private String hubName;
	private Integer sequence;
}
