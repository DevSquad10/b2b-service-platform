package com.devsquad10.order.domain.enums;

import java.util.Arrays;

public enum OrderStatus {
	ORDER_RECEIVED("ORDER_RECEIVED"), // 주문 접수
	OUT_OF_STOCK("OUT_OF_STOCK"), // 재고 부족
	PREPARING_SHIPMENT("PREPARING_SHIPMENT"), // 배송 준비 중
	INVALID_RECIPIENT("INVALID_RECIPIENT"),
	WAITING_FOR_SHIPMENT("WAITING_FOR_SHIPMENT"), // 배송 대기
	SHIPPED("SHIPPED"), // 배송 출발
	DELIVERED("DELIVERED"); // 배송 완료

	private final String status;

	OrderStatus(String status) {
		this.status = status;
	}

	// 문자열을 받아서 해당하는 Enum을 반환하는 정적 메서드
	public static OrderStatus fromString(String status) {
		return Arrays.stream(OrderStatus.values())
			.filter(orderStatus -> orderStatus.status.equalsIgnoreCase(status))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Unknown OrderStatus: " + status));
	}
}
