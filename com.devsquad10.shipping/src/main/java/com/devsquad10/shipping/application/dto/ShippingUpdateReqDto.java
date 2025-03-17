package com.devsquad10.shipping.application.dto;

import java.util.UUID;

import com.devsquad10.shipping.domain.enums.ShippingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingUpdateReqDto {

	private ShippingStatus status;

	private UUID orderId;

	private String address;
	private String requestDetails;

	private String recipientName;
	private String recipientPhone;

	private UUID companyShippingManagerId;
}
