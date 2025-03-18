package com.devsquad10.order.application.endpoint;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.devsquad10.order.application.dto.message.StockDecrementMessage;
import com.devsquad10.order.application.service.OrderService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderEndPoint {

	private final OrderService orderService;

	@RabbitListener(queues = "${stockMessage.queue.stock.response}")
	public void handlerStockDecrementResponse(StockDecrementMessage stockDecrementMessage) {
		if (stockDecrementMessage.getStatus().equals("SUCCESS")) {
			orderService.handlerShippingRequest(stockDecrementMessage);
		} else if (stockDecrementMessage.getStatus().equals("OUT_OF_STOCK")) {
			orderService.updateOrderStatus(stockDecrementMessage);
		}
	}
}
