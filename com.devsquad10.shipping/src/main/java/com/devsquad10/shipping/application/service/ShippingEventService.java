package com.devsquad10.shipping.application.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsquad10.shipping.application.dto.message.ShippingCreateRequest;
import com.devsquad10.shipping.application.dto.message.ShippingCreateResponse;
import com.devsquad10.shipping.application.service.allocation.ShippingAgentAllocation;
import com.devsquad10.shipping.application.service.message.ShippingMessageService;
import com.devsquad10.shipping.domain.enums.ShippingHistoryStatus;
import com.devsquad10.shipping.domain.enums.ShippingStatus;
import com.devsquad10.shipping.domain.model.Shipping;
import com.devsquad10.shipping.domain.model.ShippingHistory;
import com.devsquad10.shipping.domain.repository.ShippingHistoryRepository;
import com.devsquad10.shipping.domain.repository.ShippingRepository;
import com.devsquad10.shipping.infrastructure.client.HubClient;
import com.devsquad10.shipping.infrastructure.client.HubFeignClientGetRequest;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ShippingEventService {

	private final ShippingRepository shippingRepository;
	private final ShippingHistoryRepository shippingHistoryRepository;
	private final ShippingAgentAllocation shippingAgentAllocation;
	private final ShippingMessageService shippingMessageService;
	private final HubClient hubClient;

	// TODO: 권한 확인 - MASTER
	@CachePut(cacheNames = "shippingCache", key = "#result.id")
	@RabbitListener(queues = "${shippingMessage.queue.shipping.request}")
	public void handlerShippingCreateRequest(ShippingCreateRequest shippingCreateRequest) {
		// 1.주문:reqMessage(주문Id,공급업체,수령업체,주소,요청사항,납기일자)
		UUID orderId = shippingCreateRequest.getOrderId();
		UUID supplierId = shippingCreateRequest.getSupplierId();
		UUID recipientsId = shippingCreateRequest.getRecipientsId();
		String address = shippingCreateRequest.getAddress();
		String requestDetails = shippingCreateRequest.getRequestDetails();
		Date deadLine = shippingCreateRequest.getDeadLine();

		if(orderId != null
			&& supplierId != null
			&& recipientsId != null
			&& address != null
			&& deadLine != null
		) {
			log.warn("배송에 필요한 필수 정보 일부가 누락되었습니다.");
			throw new IllegalArgumentException("배송에 필요한 정보 일부가 누락됐습니다.");
		}

		// TODO: 2.업체: 각각 매개변수(공급업체ID, 수령업체ID) 조회(Feign Client 통신)
		//  -> 출발허브ID+업체담당자ID, 도착허브ID+업체담당자ID 추출
		// TODO: feign Client는 try-catch로 RuntimeException 예외 처리
		UUID departureHubId = UUID.randomUUID();
		UUID destinationHubId = UUID.randomUUID();
		// UUID departureHubId = companyClient.getHubIdIfCompanyExists(shippingCreateRequest.getSupplierId());
		// UUID destinationHubId = companyClient.getHubIdIfCompanyExists(shippingCreateRequest.getRecipientsId());

		// TODO: 도착허브에서 수령업체의 담당자ID로 User feign client 이름 조회하여 수령인 이름 사용

		// TODO: 출발허브, 도착허브, 주문id, 주소, 요청사항, 수령인, 수령인 슬랙id
		Shipping shipping = Shipping.builder()
			.status(ShippingStatus.HUB_WAIT)
			.departureHubId(departureHubId)
			.destinationHubId(destinationHubId)
			.orderId(orderId)
			.address(address)
			.requestDetails(requestDetails.equals("null") ? shippingCreateRequest.getRequestDetails() : "")
			// TODO: User feign client 사용자ID로 조회하여 이름&슬랙ID 추출
			.recipientName("수령인 이름")
			.recipientSlackId("수령인 번호")
			// TODO: shipping의 status가 HUB_ARV 될때 event 발생하여 업체 배송담당자 배정처리
			.companyShippingManagerId(null)
			.build();

		Shipping savedShipping = shippingRepository.save(shipping);

		// TODO: 4.배송 경로기록 생성: 허브간 이동정보 feign client 매개변수(출발/도착허브 ID)와 일치하는 예상거리, 소요시간, 경유지(List) 추출
		// 허브간 이동정보(하) 구현 시, 배송 허브 순번 1 고정 & 허브간 이동정보(상) 구현 시, 경유지 엔티티 추가 생성
		List<HubFeignClientGetRequest> hubRouteInfo = hubClient.getHubRouteInfo(departureHubId, destinationHubId);
		if(hubRouteInfo == null || hubRouteInfo.isEmpty()) {
			log.error("허브간 이동정보가 존재하지 않습니다.");
			throw new EntityNotFoundException("허브간 이동정보가 존재하지 않습니다.");
		}

		hubRouteInfo.sort(Comparator.comparingInt(HubFeignClientGetRequest::getSequence));

		// TODO: 허브간 경로이동 생성 전, 허브 배송담당자 배정
		UUID hubShippingAgentId = shippingAgentAllocation
			.allocateHubAgent(destinationHubId, savedShipping.getStatus())
			.getShippingManagerId();
		if(hubShippingAgentId == null) {
			log.error("배정 가능한 허브배송담당자가 존재하지 않습니다.");
			throw new EntityNotFoundException("배정 가능한 허브배송담당자가 존재하지 않습니다");
		}

		List<ShippingHistory> shippingHistories = new ArrayList<>();

		for(HubFeignClientGetRequest route : hubRouteInfo) {
			ShippingHistory shippingHistory = ShippingHistory.builder()
				.shipping(savedShipping)
				.shippingPathSequence(route.getSequence())
				.departureHubId(route.getDepartureHubId())
				.destinationHubId(route.getDestinationHubId())
				.shippingManagerId(hubShippingAgentId)
				.estiDist(route.getDistance())
				.estTime(route.getTime())
				// TODO: 실제 거리 및 시간 계산은 현재 위치 기반으로 정보를 수집하여 update 처리
				.actDist(123445.34)
				.actTime(132421)
				.historyStatus(ShippingHistoryStatus.HUB_WAIT)
				.build();
			shippingHistories.add(shippingHistory);
		}
		shippingHistoryRepository.saveAll(shippingHistories);

		// 배송/배송경로기록 생성 완료 -> 주문에 전달할 response
		try {
			shippingMessageService.sendShippingCreateMessage(savedShipping.toShippingCreateMessage());
		} catch (Exception e) {
			log.error("배송 생성 실패: {}", e.getMessage());
			ShippingCreateResponse rollbackMessage = new ShippingCreateResponse();
			rollbackMessage.setOrderId(shippingCreateRequest.getOrderId());
			rollbackMessage.setStatus("FAIL");
			// 배송 생성 실패 시 보상 트랜잭션 메시지 발행
			sendShippingCreateRollbackMessage(rollbackMessage);
			throw new RuntimeException("배송 생성 실패: " + e.getMessage(), e);
		}
	}

	// 배송생성 예외 발생 시, status FAIL 전송하여 주문생성 롤백 처리 구현 필요!
	public void sendShippingCreateRollbackMessage(ShippingCreateResponse rollbackMessage) {
		try {
			shippingMessageService.sendShippingCreateRollbackMessage(rollbackMessage);
		} catch (Exception e) {
			log.error("배송 생성 실패로 롤백 메시지 발행 실패: {}", e.getMessage());
			throw new RuntimeException("배송 생성 실패 후, 보상 트랜잭션 메시지 발행 실패:" + e.getMessage(), e);
		}
	}
}
