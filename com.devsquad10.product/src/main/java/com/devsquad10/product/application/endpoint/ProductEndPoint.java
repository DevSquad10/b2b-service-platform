package com.devsquad10.product.application.endpoint;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.devsquad10.product.application.dto.message.StockDecrementMessage;
import com.devsquad10.product.application.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductEndPoint {

	private final ProductService productService;

	@RabbitListener(queues = "${message.queue.stock}")
	public void handleStockDecrementRequest(StockDecrementMessage stockDecrementMessage) {
		productService.decreaseStock(stockDecrementMessage);
	}
}
