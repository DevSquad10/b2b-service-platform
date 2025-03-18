package com.devsquad10.order.application.service;

import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsquad10.order.application.dto.OrderReqDto;
import com.devsquad10.order.application.dto.OrderResDto;
import com.devsquad10.order.application.dto.message.StockDecrementMessage;
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

	private final OrderRepository orderRepository;
	private final RabbitTemplate rabbitTemplate;

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

	// 3. 배송 ID 는 배송에서 처리가 완료되면 받은 ID 값 등록
	// 배송 준비 중 PREPARING_SHIPMENT ( 배송 생성 message 전달 후 상태 변경)
	// 배송 대기 중 WAITING_FOR_SHIPMENT ( 납품일자 전까지 대기로 상태 변경 )
	// 배송 출발 SHIPPED ( 배송납품일 당일 6시 슬랙 메시지 전달 후 배송 출발로 상태 변경 )
	// 배송 완료 DELIVERED ( 배송 예상 시간이 되면 완료로 상태 변경 )
	public void handlerShippingRequest(StockDecrementMessage stockDecrementMessage) {
		Order targetOrder = orderRepository.findByIdAndDeletedAtIsNull(stockDecrementMessage.getOrderId())
			.orElseThrow(
				() -> new OrderNotFoundException("Order Not Found By Id : " + stockDecrementMessage.getOrderId()));

		orderRepository.save(targetOrder.toBuilder()
			.shippingId(stockDecrementMessage.getSupplierId())
			.totalAmount(stockDecrementMessage.getPrice() * targetOrder.getQuantity())
			.status(OrderStatus.PREPARING_SHIPMENT)
			.build());

		// rabbitTemplate.convertAndSend();
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
