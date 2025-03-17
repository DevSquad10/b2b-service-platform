package com.devsquad10.hub.application.dto.res;

import java.util.List;

import org.springframework.data.domain.Page;

import com.devsquad10.hub.application.dto.enums.HubRouteSortOption;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PagedHubRouteResponseDto {
	private List<PagedHubRouteItemResponseDto> hubRoutes;
	private int totalPages;
	private long totalElements;
	private int pageSize;
	private int currentPage;
	private boolean isFirst;
	private boolean isLast;
	private boolean hasNext;
	private boolean hasPrevious;
	private HubRouteSortOption sortOption;

	public static PagedHubRouteResponseDto toResponseDto(
		Page<PagedHubRouteItemResponseDto> page,
		HubRouteSortOption sortOption
	) {
		return PagedHubRouteResponseDto.builder()
			.hubRoutes(page.getContent())
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
