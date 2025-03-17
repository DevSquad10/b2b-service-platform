package com.devsquad10.shipping.application.dto;

import java.io.Serializable;
import java.util.UUID;

import com.devsquad10.shipping.domain.enums.ShippingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingResDto implements Serializable {

	private UUID id;

	private ShippingStatus status;

	private UUID orderId;
}
