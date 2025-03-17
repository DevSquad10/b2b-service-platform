package com.devsquad10.hub.presentation.req;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Sort;

import com.devsquad10.hub.presentation.enums.HubSortOption;

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
public class HubSearchRequestDto {

	private UUID id;

	private String name;

	private String address;

	@Builder.Default
	private Integer size = 10;

	@Builder.Default
	private Integer page = 0;

	// TODO: Sort Option 관련 예외 처리
	@Builder.Default
	private HubSortOption sortOption = HubSortOption.CREATED_AT;

	@Builder.Default
	private Sort.Direction sortOrder = Sort.Direction.DESC;

	public int getPage() {
		return (page != null && page > 0) ? page - 1 : 0;
	}

	public int getSize() {
		return Optional.ofNullable(size)
			.filter(s -> Set.of(10, 30, 50).contains(s))
			.orElse(10);
	}
}
