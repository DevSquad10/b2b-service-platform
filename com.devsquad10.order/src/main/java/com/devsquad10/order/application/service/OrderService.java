package com.devsquad10.order.application.service;

import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsquad10.order.application.client.CompanyClient;
import com.devsquad10.order.application.dto.OrderReqDto;
import com.devsquad10.order.application.dto.OrderResDto;
import com.devsquad10.order.application.dto.message.StockDecrementMessage;
import com.devsquad10.order.application.dto.message.StockReversalMessage;
import com.devsquad10.order.application.exception.OrderNotFoundException;
import com.devsquad10.order.domain.enums.OrderStatus;
import com.devsquad10.order.domain.model.Order;
import com.devsquad10.order.domain.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

	@Value("${stockMessage.queue.stock.request}")
	private String queueRequestStock;

	@Value("${stockMessage.queue.stockRecovery.request}")
	private String queueStockRecovery;

	private final OrderRepository orderRepository;
	private final RabbitTemplate rabbitTemplate;
	private final CompanyClient companyClient;

	public void createOrder(OrderReqDto orderReqDto) {
		Order order = Order.builder()
			.recipientsId(orderReqDto.getRecipientsId())
			.productId(orderReqDto.getProductId())
			.productName(orderReqDto.getProductName())
			.quantity(orderReqDto.getQuantity())
			.requestDetails(orderReqDto.getRequestDetails())
			.deadLine(orderReqDto.getDeadLine())
			.status(OrderStatus.ORDER_RECEIVED)
			.build();

		// DB에 저장
		orderRepository.save(order);

		sendStockDecrementMessage(order.toStockDecrementMessage());

	}

	@Transactional(readOnly = true)
	public OrderResDto getOrderById(UUID id) {
		return orderRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new OrderNotFoundException("Order Not Found By Id : " + id))
			.toResponseDto();
	}

	@Transactional(readOnly = true)
	public Page<OrderResDto> searchOrders(String q, String category, int page, int size, String sort, String order) {

		Page<Order> orderPages = orderRepository.findAll(q, category, page, size, sort, order);

		return orderPages.map(Order::toResponseDto);

	}

	private void sendStockDecrementMessage(StockDecrementMessage stockDecrementMessage) {
		rabbitTemplate.convertAndSend(queueRequestStock, stockDecrementMessage);
	}

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
