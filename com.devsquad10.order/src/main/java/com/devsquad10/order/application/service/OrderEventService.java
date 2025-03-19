package com.devsquad10.order.application.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.devsquad10.order.application.client.CompanyClient;
import com.devsquad10.order.application.dto.message.StockDecrementMessage;
import com.devsquad10.order.application.dto.message.StockReversalMessage;
import com.devsquad10.order.application.exception.OrderNotFoundException;
import com.devsquad10.order.domain.enums.OrderStatus;
import com.devsquad10.order.domain.model.Order;
import com.devsquad10.order.domain.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderEventService {

	@Value("${stockMessage.queue.stockRecovery.request}")
	private String queueStockRecovery;

	private final RabbitTemplate rabbitTemplate;
	private final OrderRepository orderRepository;
	private final CompanyClient companyClient;

	public void handlerShippingRequest(StockDecrementMessage stockDecrementMessage) {
		Order targetOrder = orderRepository.findByIdAndDeletedAtIsNull(stockDecrementMessage.getOrderId())
			.orElseThrow(
				() -> new OrderNotFoundException("Order Not Found By Id : " + stockDecrementMessage.getOrderId()));

		String recipientsAddress = companyClient.getCompanyAddress(targetOrder.getRecipientsId());
		if (recipientsAddress != null) {
			// 배송 준비 중 상태
			updateOrderStatusAndShippingDetails(targetOrder, stockDecrementMessage, OrderStatus.PREPARING_SHIPMENT);

			//배송에 보낼 메시지 생생 (공급업체, 수량업체,  업체 주소, orderId, 요청 사항)
			// rabbitTemplate.convertAndSend(...);
		} else {
			updateOrderStatusAndShippingDetails(targetOrder, stockDecrementMessage, OrderStatus.INVALID_RECIPIENT);

			StockReversalMessage stockReversalMessage = new StockReversalMessage(targetOrder.getProductId(),
				targetOrder.getQuantity());
			// 재고 감소 복구 요청
			rabbitTemplate.convertAndSend(queueStockRecovery, stockReversalMessage);
		}
	}

	private void updateOrderStatusAndShippingDetails(Order targetOrder, StockDecrementMessage stockDecrementMessage,
		OrderStatus newStatus) {
		orderRepository.save(targetOrder.toBuilder()
			.shippingId(stockDecrementMessage.getSupplierId())
			.totalAmount(stockDecrementMessage.getPrice() * targetOrder.getQuantity())
			.status(newStatus)
			.build());
	}

	public void updateOrderStatus(StockDecrementMessage stockDecrementMessage) {
		Order targetOrder = orderRepository.findByIdAndDeletedAtIsNull(stockDecrementMessage.getOrderId())
			.orElseThrow(
				() -> new OrderNotFoundException("Order Not Found By Id : " + stockDecrementMessage.getOrderId()));

		orderRepository.save(targetOrder.toBuilder()
			.status(OrderStatus.fromString(stockDecrementMessage.getStatus()))
			.build());
	}
}
