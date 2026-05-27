package com.devsquad10.order.application.service;

import com.devsquad10.order.application.dto.message.ShippingCreateRequest;
import com.devsquad10.order.application.dto.message.ShippingUpdateRequest;
import com.devsquad10.order.application.dto.message.StockDecrementMessage;
import com.devsquad10.order.application.dto.message.StockReversalMessage;
import com.devsquad10.order.application.messaging.OrderMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderMessageServiceImpl implements OrderMessageService {
    private final RabbitTemplate rabbitTemplate;

    // 재고 감소 요청 큐에 대한 설정값
    @Value("${stockMessage.exchange.stock.request}")
    private String stockRequestExchange;

    // 재고 복원 요청 큐에 대한 설정값
    @Value("${stockMessage.queue.stockRecovery.request}")
    private String queueStockRecovery;

    // 배송 생성 요청 큐에 대한 설정값
    @Value("${shippingMessage.queue.shipping.request}")
    private String queueShippingCreateRequest;

    // 배송 수정 요청 큐에 대한 설정값
    @Value("${shippingMessage.queue.shipping_update.request}")
    private String queueShippingUpdateRequest;

    @Override
    public void sendStockDecrementMessage(StockDecrementMessage stockDecrementMessage) {
        // 1. 도메인 키(productId)를 문자열 라우팅 키로 변환
        String routingKey = String.valueOf(stockDecrementMessage.getProductId());

        // 2. Queue가 아닌 Exchange로 전송하며, routingKey를 명시함
        rabbitTemplate.convertAndSend(stockRequestExchange, routingKey, stockDecrementMessage);
    }

    @Override
    public void sendStockReversalMessage(StockReversalMessage stockReversalMessage) {
        rabbitTemplate.convertAndSend(queueStockRecovery, stockReversalMessage);
    }

    @Override
    public void sendShippingCreateMessage(ShippingCreateRequest shippingCreateRequest) {
        rabbitTemplate.convertAndSend(queueShippingCreateRequest, shippingCreateRequest);
    }

    @Override
    public void sendShippingUpdateMessage(ShippingUpdateRequest shippingUpdateRequest) {
        rabbitTemplate.convertAndSend(queueShippingUpdateRequest, shippingUpdateRequest);
    }
}
