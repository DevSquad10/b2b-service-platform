package com.devsquad10.product.application.endpoint;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.devsquad10.product.application.dto.message.StockDecrementMessage;
import com.devsquad10.product.application.dto.message.StockReversalMessage;
import com.devsquad10.product.application.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductEndPoint {

	private final ProductService productService;

	@RabbitListener(queues = "${stockMessage.queue.stock.request}")
	public void handleStockDecrementRequest(StockDecrementMessage stockDecrementMessage) {
		productService.decreaseStock(stockDecrementMessage);
	}

	@RabbitListener(queues = "${stockMessage.queue.stockRecovery.request}")
	public void handlerStockRecoveryRequest(StockReversalMessage stockReversalMessage) {
		productService.recoveryStock(stockReversalMessage);
	}
}
