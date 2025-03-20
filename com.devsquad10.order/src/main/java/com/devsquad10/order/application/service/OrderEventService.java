package com.devsquad10.order.application.service;

import org.springframework.stereotype.Service;

import com.devsquad10.order.application.client.CompanyClient;
import com.devsquad10.order.application.dto.message.ShippingCreateRequest;
import com.devsquad10.order.application.dto.message.StockDecrementMessage;
import com.devsquad10.order.application.dto.message.StockReversalMessage;
import com.devsquad10.order.application.exception.OrderNotFoundException;
import com.devsquad10.order.application.messaging.OrderMessageService;
import com.devsquad10.order.domain.enums.OrderStatus;
import com.devsquad10.order.domain.model.Order;
import com.devsquad10.order.domain.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderEventService {

	private final OrderRepository orderRepository;
	private final CompanyClient companyClient;
	private final OrderMessageService orderMessageService;

	public void handlerShippingRequest(StockDecrementMessage stockDecrementMessage) {
		Order targetOrder = orderRepository.findByIdAndDeletedAtIsNull(stockDecrementMessage.getOrderId())
			.orElseThrow(
				() -> new OrderNotFoundException("Order Not Found By Id : " + stockDecrementMessage.getOrderId()));

		String recipientsAddress = companyClient.getCompanyAddress(targetOrder.getRecipientsId());
		if (recipientsAddress != null) {
			// 배송 준비 중 상태
			updateOrderStatusAndShippingDetails(targetOrder, stockDecrementMessage, OrderStatus.PREPARING_SHIPMENT);

			ShippingCreateRequest shippingCreateRequest = new ShippingCreateRequest(targetOrder.getId(),
				targetOrder.getSupplierId(), targetOrder.getRecipientsId(), recipientsAddress,
				targetOrder.getRequestDetails(), targetOrder.getDeadLine());

			orderMessageService.sendShippingCreateMessage(shippingCreateRequest);
		} else {
			updateOrderStatusAndShippingDetails(targetOrder, stockDecrementMessage, OrderStatus.INVALID_RECIPIENT);

			StockReversalMessage stockReversalMessage = new StockReversalMessage(targetOrder.getProductId(),
				targetOrder.getQuantity());
			// 재고 감소 복구 요청
			orderMessageService.sendStockReversalMessage(stockReversalMessage);
		}
	}

	private void updateOrderStatusAndShippingDetails(Order targetOrder, StockDecrementMessage stockDecrementMessage,
		OrderStatus newStatus) {
		orderRepository.save(targetOrder.toBuilder()
			.supplierId(stockDecrementMessage.getSupplierId())
			.productName(stockDecrementMessage.getProductName())
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
