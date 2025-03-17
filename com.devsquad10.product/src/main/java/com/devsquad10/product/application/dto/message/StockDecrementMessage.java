package com.devsquad10.product.application.dto.message;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDecrementMessage {
	private UUID orderId;

	private UUID productId;

	private Integer quantity;

	private String status;

	private Integer price;
}
