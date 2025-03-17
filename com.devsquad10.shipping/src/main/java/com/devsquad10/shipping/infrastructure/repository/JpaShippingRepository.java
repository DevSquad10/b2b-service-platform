package com.devsquad10.shipping.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.devsquad10.shipping.domain.model.QShipping;
import com.devsquad10.shipping.domain.model.Shipping;
import com.devsquad10.shipping.domain.repository.ShippingRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.ComparableExpressionBase;

@Repository
public interface JpaShippingRepository
	extends JpaRepository<Shipping, UUID>,
	QuerydslPredicateExecutor<Shipping>,
	ShippingRepository {

	default Page<Shipping> findAll(String query, String category, int page, int size, String sortBy, String order) {

		QShipping shipping = QShipping.shipping;

		// 검색 조건 생성
		BooleanBuilder builder = buildSearchConditions(query, category, shipping);

		Sort sort = getSortOrder(sortBy, order);

		PageRequest pageRequest = PageRequest.of(page, size, sort);

		return findAll(builder, pageRequest);
	}


	private BooleanBuilder buildSearchConditions(String query, String category, QShipping qShipping) {

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(qShipping.deletedBy.isNull());

		if (query == null || query.isEmpty()) {
			return builder;
		}

		if (category == null || category.isEmpty()) {
			// 카테고리 지정이 없으면 모든 필드에서 검색
			builder.or(parseUUID(query,qShipping.departureHubId));
			builder.or(parseUUID(query,qShipping.companyShippingManagerId));
			builder.or(qShipping.status.stringValue().containsIgnoreCase(query));
		} else {
			switch (category) {
				case "departureHubId":
					builder.or(parseUUID(query,qShipping.departureHubId));
					break;
				case "companyShippingManagerId":
					builder.or(parseUUID(query,qShipping.companyShippingManagerId));;
					break;
				case "status":
					builder.or(qShipping.status.stringValue().containsIgnoreCase(query));
					break;
				default:
					builder.or(parseUUID(query,qShipping.departureHubId));
					builder.or(parseUUID(query,qShipping.companyShippingManagerId));;
					builder.or(qShipping.status.stringValue().containsIgnoreCase(query));
					break;
			}
		}
		return builder;
	}

	private BooleanBuilder parseUUID(String query, ComparableExpressionBase<UUID> uuidField) {
		try {
			UUID uuidQuery = UUID.fromString(query);
			return new BooleanBuilder(uuidField.eq(uuidQuery));
		} catch (IllegalArgumentException e) {
			return new BooleanBuilder(); // 잘못된 uuid이면 빈 조건 검색 반환 (검색 무시)
		}
	}

	private Sort getSortOrder(String sortBy, String order) {
		if (!isValidSortBy(sortBy)) {
			throw new IllegalArgumentException("SortBy 는 'createdAt', 'updatedAt', 'deletedAt' 값만 허용합니다.");
		}

		Sort sort = Sort.by(Sort.Order.by(sortBy));
		sort = getSortDirection(sort, order);

		return sort;
	}

	private boolean isValidSortBy(String sortBy) {
		return "createdAt".equals(sortBy) || "updatedAt".equals(sortBy) || "deletedAt".equals(sortBy);
	}

	private Sort getSortDirection(Sort sort, String order) {
		return "desc".equals(order) ? sort.descending() : sort.ascending();
	}
}
