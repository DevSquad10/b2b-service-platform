package com.devsquad10.order.application.endpoint;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.devsquad10.order.application.dto.message.ShippingCreateResponse;
import com.devsquad10.order.application.dto.message.ShippingUpdateResponse;
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

	// 배차 실패 시 는 대기 상태로 변경
	// 다시 요청?
	@RabbitListener(queues = "${shippingMessage.queue.shipping.response}")
	public void handlerShippingCreateResponse(ShippingCreateResponse shippingCreateResponse) {
		if (shippingCreateResponse.getStatus().equals("SUCCESS")) {
			orderEventService.updateOrderStatusToWaitingForShipment(shippingCreateResponse);
		} else if (shippingCreateResponse.getStatus().equals("FAIL")) {
			orderEventService.retryCreateShipping(shippingCreateResponse);
		}
	}

	@RabbitListener(queues = "${shippingMessage.queue.shipping_update.response}")
	public void handlerShippingUpdateResponse(ShippingUpdateResponse updateResponse) {

	}

}
