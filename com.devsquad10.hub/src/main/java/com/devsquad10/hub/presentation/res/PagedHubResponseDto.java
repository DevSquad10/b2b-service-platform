package com.devsquad10.hub.presentation.res;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;

import com.devsquad10.hub.presentation.enums.HubSortOption;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PagedHubResponseDto implements Serializable {
	private List<PagedHubItemResponseDto> hubs;
	private int totalPages;
	private long totalElements;
	private int pageSize;
	private int currentPage;
	private boolean isFirst;
	private boolean isLast;
	private boolean hasNext;
	private boolean hasPrevious;
	private HubSortOption sortOption;

	public static PagedHubResponseDto toResponseDto(
		Page<PagedHubItemResponseDto> page,
		HubSortOption sortOption
	) {
		return PagedHubResponseDto.builder()
			.hubs(page.getContent())
			.totalPages(page.getTotalPages())
			.totalElements(page.getTotalElements())
			.pageSize(page.getSize())
			.currentPage(page.getNumber() + 1)
			.isFirst(page.isFirst())
			.isLast(page.isLast())
			.hasNext(page.hasNext())
			.hasPrevious(page.hasPrevious())
			.sortOption(sortOption)
			.build();
	}
}
