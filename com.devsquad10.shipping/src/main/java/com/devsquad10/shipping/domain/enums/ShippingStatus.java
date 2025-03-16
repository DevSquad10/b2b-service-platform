package com.devsquad10.shipping.domain.enums;

public enum ShippingStatus {
	HUB_WAIT("HUB_WAIT"),
	HUB_TRNS("HUB_TRNS"),
	HUB_ARV("HUB_ARV"),
	COM_TRNS("COM_TRNS"),
	DLV_CMP("DLV_CMP");

	private final String status;

	ShippingStatus(String status) {
		this.status = status;
	}
}
