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

}
