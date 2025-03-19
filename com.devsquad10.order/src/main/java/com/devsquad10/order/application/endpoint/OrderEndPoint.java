package com.devsquad10.order.application.endpoint;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.devsquad10.order.application.dto.message.StockDecrementMessage;
import com.devsquad10.order.application.service.OrderEventService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderEndPoint {

	private final OrderEventService orderEventService;

	@RabbitListener(queues = "${stockMessage.queue.stock.response}")
	public void handlerStockDecrementResponse(StockDecrementMessage stockDecrementMessage) {
		if (stockDecrementMessage.getStatus().equals("SUCCESS")) {
			orderEventService.handlerShippingRequest(stockDecrementMessage);
		} else if (stockDecrementMessage.getStatus().equals("OUT_OF_STOCK")) {
			orderEventService.updateOrderStatus(stockDecrementMessage);
		}
	}
}
