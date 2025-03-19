package com.devsquad10.shipping.infrastructure.client;

import java.util.UUID;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingAgentPatchFeignRequest {

	// 유저:기본키(id) = 배송담당자 ID(shipping_manager_id)
	private UUID id;

	private String slackId;

	private UUID hubId;
}
