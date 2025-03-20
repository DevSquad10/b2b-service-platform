package com.devsquad10.order.application.service;

import java.util.Optional;

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

		Optional<String> recipientsAddress = Optional.ofNullable(
			companyClient.getCompanyAddress(targetOrder.getRecipientsId()));

		recipientsAddress.ifPresentOrElse(
			address -> processValidRecipient(targetOrder, stockDecrementMessage, address),
			() -> processInvalidRecipient(targetOrder, stockDecrementMessage)

		);
	}

	public void updateOrderStatus(StockDecrementMessage stockDecrementMessage) {
		Order targetOrder = orderRepository.findByIdAndDeletedAtIsNull(stockDecrementMessage.getOrderId())
			.orElseThrow(
				() -> new OrderNotFoundException("Order Not Found By Id : " + stockDecrementMessage.getOrderId()));

		orderRepository.save(targetOrder.toBuilder()
			.status(OrderStatus.fromString(stockDecrementMessage.getStatus()))
			.build());
	}

	private void processValidRecipient(Order targetOrder, StockDecrementMessage stockDecrementMessage, String address) {
		updateOrderStatusAndShippingDetails(targetOrder, stockDecrementMessage, OrderStatus.PREPARING_SHIPMENT);

		ShippingCreateRequest shippingCreateRequest = createShippingRequest(targetOrder, stockDecrementMessage,
			address);
		orderMessageService.sendShippingCreateMessage(shippingCreateRequest);
	}

	private void processInvalidRecipient(Order targetOrder, StockDecrementMessage stockDecrementMessage) {
		updateOrderStatusAndShippingDetails(targetOrder, stockDecrementMessage, OrderStatus.INVALID_RECIPIENT);

		StockReversalMessage stockReversalMessage = new StockReversalMessage(targetOrder.getProductId(),
			targetOrder.getQuantity());
		orderMessageService.sendStockReversalMessage(stockReversalMessage);
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

	private ShippingCreateRequest createShippingRequest(Order order, StockDecrementMessage message, String address) {
		return new ShippingCreateRequest(
			order.getId(),
			message.getSupplierId(),
			order.getRecipientsId(),
			address,
			order.getRequestDetails(),
			order.getDeadLine()
		);
	}
}
