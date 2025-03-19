package com.devsquad10.shipping.application.dto.response;

import java.io.Serializable;
import java.util.UUID;

import com.devsquad10.shipping.domain.enums.ShippingAgentType;
import com.devsquad10.shipping.domain.model.ShippingAgent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingAgentResDto implements Serializable {

	private UUID id;

	private UUID hubId;

	private UUID shippingManagerId;

	private String shippingManagerSlackId;

	private ShippingAgentType type;

	// TODO: 배정에 사용할 예정, 조회될 때 front 에서 불필요
	private Integer shippingSequence;

	private Boolean isTransit;

	public static ShippingAgentResDto toResponse(ShippingAgent shippingAgent) {
		return ShippingAgentResDto.builder()
			.id(shippingAgent.getId())
			.hubId(shippingAgent.getHubId())
			.shippingManagerId(shippingAgent.getShippingManagerId())
			.shippingManagerSlackId(shippingAgent.getShippingManagerSlackId())
			.shippingSequence(shippingAgent.getShippingSequence())
			.type(shippingAgent.getType())
			.isTransit(shippingAgent.getIsTransit())
			.build();
	}
}
