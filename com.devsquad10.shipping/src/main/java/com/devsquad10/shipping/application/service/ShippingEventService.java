package com.devsquad10.shipping.application.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsquad10.shipping.application.dto.message.ShippingCreateRequest;
import com.devsquad10.shipping.application.dto.message.ShippingCreateResponse;
import com.devsquad10.shipping.application.exception.shipping.ShippingCreateException;
import com.devsquad10.shipping.application.service.allocation.ShippingAgentAllocation;
import com.devsquad10.shipping.application.service.message.ShippingMessageService;
import com.devsquad10.shipping.domain.enums.ShippingHistoryStatus;
import com.devsquad10.shipping.domain.enums.ShippingStatus;
import com.devsquad10.shipping.domain.model.Shipping;
import com.devsquad10.shipping.domain.model.ShippingHistory;
import com.devsquad10.shipping.domain.repository.ShippingHistoryRepository;
import com.devsquad10.shipping.domain.repository.ShippingRepository;
import com.devsquad10.shipping.infrastructure.client.CompanyClient;
import com.devsquad10.shipping.infrastructure.client.HubClient;
import com.devsquad10.shipping.infrastructure.client.HubFeignClientGetRequest;
import com.devsquad10.shipping.infrastructure.client.ShippingCompanyInfoDto;
import com.devsquad10.shipping.infrastructure.client.UserClient;
import com.devsquad10.shipping.infrastructure.client.UserInfoFeignClientRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

import feign.FeignException;
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
	private final CompanyClient companyClient;
	private final UserClient userClient;

	// TODO: 권한 확인 - MASTER
	public void handlerShippingCreateRequest(ShippingCreateRequest shippingCreateRequest) throws
		JsonProcessingException {
		// 주문:reqMessage(주문Id,공급업체,수령업체,주소,요청사항,납기일자)
		// company feign Client로 공급업체 정보 조회
		ShippingCompanyInfoDto supplierIdInfo = getSupplierInfo(shippingCreateRequest.getSupplierId(), shippingCreateRequest);

		// company feign Client로 수령업체 정보 조회
		ShippingCompanyInfoDto recipientsInfo = getRecipientsInfo(shippingCreateRequest.getRecipientsId(), shippingCreateRequest);

		// 도착허브Id에서 수령업체의 담당자ID로 User feign client 이름 조회하여 수령인 이름 및 슬랙Id 추출
		UserInfoFeignClientRequest userInfo = userClient.getUserInfoRequest(recipientsInfo.getVenderId());

		Shipping shipping = Shipping.builder()
			.status(ShippingStatus.HUB_WAIT)
			.departureHubId(supplierIdInfo.getHubId())
			.destinationHubId(recipientsInfo.getHubId())
			.orderId(shippingCreateRequest.getOrderId())
			.address(shippingCreateRequest.getAddress())
			.requestDetails(shippingCreateRequest.getRequestDetails() != null ? shippingCreateRequest.getRequestDetails() : "")
			.recipientName(userInfo.getUsername())
			.recipientSlackId(userInfo.getSlackId())
			// TODO: shipping의 status가 HUB_ARV 될때 event 발생하여 업체 배송담당자 배정처리
			.companyShippingManagerId(null)
			.deadLine(shippingCreateRequest.getDeadLine())
			.build();
		Shipping savedShipping = shippingRepository.save(shipping);
		log.info("savedShipping: {}", savedShipping);

		// 배송 경로기록 생성: 허브간 이동정보 feign client 매개변수(출발/도착허브 ID)와 일치하는 예상거리, 소요시간, 경유지(List) 추출
		// 허브간 이동정보(hub-to relay-hub) 구현 시, feign client 호출 오류 발생!
		//TODO mvp를 위해 연결 : 허브간 이동정보(hub-to-hub)로 배송 허브 순번 1 고정
		// 임시로 1개만 더미 데이터 입력 함.
		//List<HubFeignClientGetRequest> hubRouteInfo = hubClient.getHubRouteInfo(supplierIdInfo.getHubId(), recipientsInfo.getHubId());
		List<HubFeignClientGetRequest> hubRouteInfo = new ArrayList<>();
		HubFeignClientGetRequest hubFeignClientGetRequest = HubFeignClientGetRequest.builder()
			.sequence(1)
			.departureHubId(supplierIdInfo.getHubId())
			.destinationHubId(recipientsInfo.getHubId())
			.time(1234235)
			.distance(1231.5234233)
			.build();
		hubRouteInfo.add(hubFeignClientGetRequest);

		if(hubRouteInfo == null || hubRouteInfo.isEmpty()) {
			log.error("허브간 이동정보가 존재하지 않습니다.");
			failErrorMessage(shippingCreateRequest);
			throw new EntityNotFoundException("허브간 이동정보가 존재하지 않습니다.");
		}

		hubRouteInfo.sort(Comparator.comparingInt(HubFeignClientGetRequest::getSequence));

		// 허브간 경로이동 생성 전, 허브 배송담당자 배정
		UUID selectedHubShippingAgentId = allocationHubShippingManagerId(recipientsInfo.getHubId());

		// 배송 경로기록 생성 및 저장
		createShippingHistory(hubRouteInfo, savedShipping, selectedHubShippingAgentId);

		// 배송/배송경로기록 생성 완료 -> 주문에 전달할 response
		try {
			log.info("배송,배송경로기록 생성 완료로 주문 메시지 발행");
			shippingMessageService.sendShippingCreateMessage(savedShipping.toShippingCreateMessage());
		} catch (Exception e) {
			log.error("배송 생성 실패: {}", e.getMessage());
			failErrorMessage(shippingCreateRequest);
			throw new RuntimeException("배송 생성 실패: " + e.getMessage(), e);
		}
	}

	// 배송생성 실패 시, status FAIL 전송하여 주문생성 롤백 처리 전달!
	public void sendShippingCreateRollbackMessage(ShippingCreateResponse rollbackMessage) {
		log.info("배송 생성 예외 발생으로 롤백 메시지 발행");
		shippingMessageService.sendShippingCreateRollbackMessage(rollbackMessage);
		throw new ShippingCreateException("배송 생성 실패");
	}

	// 배송 생성 실패 메시지 전송
	private void failErrorMessage(ShippingCreateRequest shippingCreateRequest) {
		ShippingCreateResponse rollbackMessage = new ShippingCreateResponse();
		rollbackMessage.setOrderId(shippingCreateRequest.getOrderId());
		rollbackMessage.setStatus("FAIL");
		// 배송 생성 실패 시 보상 트랜잭션 메시지 발행 요청
		sendShippingCreateRollbackMessage(rollbackMessage);
	}

	// company feign Client로 공급업체 정보 조회
	private ShippingCompanyInfoDto getSupplierInfo(UUID supplierId, ShippingCreateRequest shippingCreateRequest) {
		try {
			return companyClient.findShippingCompanyInfo(supplierId);
		} catch (FeignException.FeignClientException e) {
			log.error("업체 feign client 호출 실패로 출발허브 ID 조회 불가");
			failErrorMessage(shippingCreateRequest);
			throw new ShippingCreateException("배송 생성 실패");
		}
	}

	// company feign Client로 수령업체 정보 조회
	private ShippingCompanyInfoDto getRecipientsInfo(UUID recipientsId, ShippingCreateRequest shippingCreateRequest) {
		try {
			return companyClient.findShippingCompanyInfo(recipientsId);
		} catch (FeignException.FeignClientException e) {
			log.error("업체 feign client 호출 실패로 도착허브 정보 조회 불가");
			failErrorMessage(shippingCreateRequest);
			throw new ShippingCreateException("배송 생성 실패");
		}
	}

	// 허브간 경로이동 생성 전, 허브 배송담당자 배정
	private UUID allocationHubShippingManagerId(UUID destinationHubId) {
		UUID selectedHubShippingAgentId = shippingAgentAllocation
			.allocateHubAgent(destinationHubId)
			.getShippingManagerId();
		if (selectedHubShippingAgentId == null) {
			log.error("배정 가능한 허브배송담당자가 존재하지 않습니다.");
			throw new EntityNotFoundException("배정 가능한 허브배송담당자가 존재하지 않습니다");
		}
		return selectedHubShippingAgentId;
	}

	// 배송 경로기록 생성 및 저장
	private void createShippingHistory(
		List<HubFeignClientGetRequest> hubRouteInfo,
		Shipping savedShipping,
		UUID selectedHubShippingAgentId) {

		List<ShippingHistory> shippingHistories = new ArrayList<>();

		for(HubFeignClientGetRequest route : hubRouteInfo) {
			ShippingHistory shippingHistory = ShippingHistory.builder()
				.shipping(savedShipping)
				.shippingPathSequence(route.getSequence())
				.departureHubId(route.getDepartureHubId())
				.destinationHubId(route.getDestinationHubId())
				.shippingManagerId(selectedHubShippingAgentId)
				.estiDist(route.getDistance())
				.estTime(route.getTime())
				// TODO: 실제 거리 및 시간 계산은 현재 위치 기반으로 정보를 수집하여 update 처리
				.actDist(route.getDistance() + 2.23)
				.actTime(route.getTime() + 2342365)
				.historyStatus(ShippingHistoryStatus.HUB_WAIT)
				.build();
			shippingHistories.add(shippingHistory);
			shippingHistoryRepository.save(shippingHistory);
		}
	}

	// public void handlerOrderUpdateMessage(ShippingCreateRequest shippingCreateRequest) {
	// 	shippingMessageService.updateOrderStatusAndShippingDetails(shippingCreateRequest);
	// }
}
