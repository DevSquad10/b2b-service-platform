package com.devsquad10.order.application.service;

import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.devsquad10.order.application.dto.OrderReqDto;
import com.devsquad10.order.domain.enums.OrderStatus;
import com.devsquad10.order.domain.model.Order;
import com.devsquad10.order.domain.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	// 주문 접수 시
	public void createOrder(OrderReqDto orderReqDto) {
		// 주문 최초 접수 시 상태 ORDER_RECEIVED ( api 최초 요청 시 )
		Order order = Order.builder()
			.id(UUID.randomUUID())
			.recipientsId(orderReqDto.getRecipientsId())
			.shippingId(orderReqDto.getShippingId())
			.productId(orderReqDto.getProductId())
			.productName(orderReqDto.getProductName())
			.quantity(orderReqDto.getQuantity())
			.requestDetails(orderReqDto.getRequestDetails())
			.deadLine(orderReqDto.getDeadLine())
			.status(OrderStatus.ORDER_RECEIVED)
			.build();

		// redis 에 저장
		redisTemplate.opsForValue().set("order:" + order.getId(), order);

		// 1. 상품 ID로 상품에 접근에 quantity 만큼 수량 차감 ( 주문 취소 시 수량 복원 ) ( delete 에서 처리 )
		// 재고 부족 시 OUT_OF_STOCK ( 상품의 수량 차감 시도 시 재고가 부족한 상황 )
		// 재고 확보 대기 WAITING_FOR_STOCK ( message 로 업체에 상품 재고 부족 알림 )

		// 메시징으로 처라해야함

		// 2. 상품 ID로 해당 상품의 가격을 받아  price * quantity 계산 후 총 금액 저장

	}

	// 3. 배송 ID 는 배송에서 처리가 완료되면 받은 ID 값 등록
	// 배송 준비 중 PREPARING_SHIPMENT ( 배송 생성 message 전달 후 상태 변경)
	// 배송 대기 중 WAITING_FOR_SHIPMENT ( 납품일자 전까지 대기로 상태 변경 )
	// 배송 출발 SHIPPED ( 배송납품일 당일 6시 슬랙 메시지 전달 후 배송 출발로 상태 변경 )
	// 배송 완료 DELIVERED ( 배송 예상 시간이 되면 완료로 상태 변경 )

	// 모든 과정이 정상적으로 처리되고나서 db에 저장
}
