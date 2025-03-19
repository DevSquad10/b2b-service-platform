package com.devsquad10.shipping.application.service;

import java.util.UUID;

import com.devsquad10.shipping.application.dto.MinimumCountAllocationResult;
import com.devsquad10.shipping.domain.enums.ShippingStatus;

public interface ShippingAgentAllocationMethod {
	// 최소 배정 건수 담당자 선택
	MinimumCountAllocationResult allocationResult(UUID destinationHubId, ShippingStatus shippingStatus);

	// 확장성 측면 : 배정 횟수 가중치 고려(배송거리, 담당자 숙련도 등)
}
