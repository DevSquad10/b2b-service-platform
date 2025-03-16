package com.devsquad10.product.domain.enums;

public enum ProductStatus {
	AVAILABLE("AVAILABLE"), // 판매중
	SOLD_OUT("SOLD_OUT"), // sold out
	WAITING_FOR_STOCK("WAITING_FOR_STOCK"); // 재고 확보 대기

	private final String status;

	ProductStatus(String status) {
		this.status = status;
	}
}
