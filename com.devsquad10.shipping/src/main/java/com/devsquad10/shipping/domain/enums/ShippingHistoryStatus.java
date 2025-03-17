package com.devsquad10.shipping.domain.enums;

public enum ShippingHistoryStatus {
	HUB_WAIT("HUB_WAIT"),
	HUB_TRNS("HUB_TRNS"),
	HUB_ARV("HUB_ARV"),
	DLV_CMP("DLV_CMP");

	private final String historyStatus;

	ShippingHistoryStatus(String historyStatus) {
		this.historyStatus = historyStatus;
	}
}
