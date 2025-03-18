package com.devsquad10.shipping.domain.enums;

public enum ShippingAgentType {
	HUB_DVL("HUB_DVL"),
	COM_DVL("COM_DVL");

	private final String type;

	ShippingAgentType(String type) {
		this.type = type;
	}
}
