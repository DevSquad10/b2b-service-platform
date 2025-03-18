package com.devsquad10.order.application.dto;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderReqDto {
	
	private UUID recipientsId; // 수령업체

	private UUID productId; // 상품 ID

	private UUID shippingId; // 배송 ID;

	private String productName; // 상품명

	private Integer quantity; // 주문 수량

	private Integer totalAmount; // 총 금액

	private String requestDetails; // 요청사항

	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date deadLine; // 납품기한일자

}
