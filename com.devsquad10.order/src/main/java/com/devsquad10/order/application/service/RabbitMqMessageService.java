package com.devsquad10.order.application.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.devsquad10.order.application.dto.message.StockDecrementMessage;
import com.devsquad10.order.application.dto.message.StockReversalMessage;
import com.devsquad10.order.application.messaging.OrderMessageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RabbitMqMessageService implements OrderMessageService {
	private final RabbitTemplate rabbitTemplate;

	@Value("${stockMessage.queue.stock.request}")
	private String queueRequestStock;

	@Value("${stockMessage.queue.stockRecovery.request}")
	private String queueStockRecovery;
	
	@Override
	public void sendStockDecrementMessage(StockDecrementMessage stockDecrementMessage) {
		rabbitTemplate.convertAndSend(queueRequestStock, stockDecrementMessage);
	}

	@Override
	public void sendStockReversalMessage(StockReversalMessage stockReversalMessage) {
		rabbitTemplate.convertAndSend(queueStockRecovery, stockReversalMessage);
	}
}
