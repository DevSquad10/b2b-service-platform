package com.devsquad10.hub.presentation.res;

import java.util.UUID;

import com.devsquad10.hub.domain.model.Hub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 허브 목록의 개별 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedHubItemResponseDto {

	private UUID id;
	private String name;
	private String address;
	private Double latitude;
	private Double longitude;

	public static PagedHubItemResponseDto toResponseDto(Hub hub) {
		return PagedHubItemResponseDto.builder()
			.id(hub.getId())
			.name(hub.getName())
			.address(hub.getAddress())
			.latitude(hub.getLatitude())
			.longitude(hub.getLongitude())
			.build();
	}
}
