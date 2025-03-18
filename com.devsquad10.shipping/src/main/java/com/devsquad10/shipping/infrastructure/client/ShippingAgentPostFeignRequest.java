package com.devsquad10.shipping.infrastructure.client;

import java.util.UUID;

import com.devsquad10.shipping.domain.enums.ShippingAgentType;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingAgentPostFeignRequest {

	// 유저:기본키(id) = 배송담당자 ID(shipping_manager_id)
	private UUID id;
	private String slackId;

	@NotNull
	private UUID hubId;
	@NotNull
	private ShippingAgentType type;
}
