package com.devsquad10.shipping.application.service;

import java.util.UUID;

import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsquad10.shipping.application.dto.ShippingPostReqDto;
import com.devsquad10.shipping.application.dto.ShippingPostResDto;
import com.devsquad10.shipping.domain.enums.ShippingHistoryStatus;
import com.devsquad10.shipping.domain.enums.ShippingStatus;
import com.devsquad10.shipping.domain.model.Shipping;
import com.devsquad10.shipping.domain.model.ShippingHistory;
import com.devsquad10.shipping.domain.repository.ShippingRepository;
import com.devsquad10.shipping.domain.repository.ShippingHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShippingService {

	private final ShippingRepository shippingRepository;

	private final ShippingHistoryRepository shippingHistoryRepository;

	@CachePut(cacheNames = "shippingCache", key = "#result.id")
	public ShippingPostResDto createShipping(ShippingPostReqDto shippingPostReqDto) {
		//TODO: 1.주문: reqMessage(공급업체ID, 수령업체ID, 배송지 주소, 요청사항) 받기

		//TODO: 2.업체: 각각 매개변수(공급업체ID, 수령업체ID) 조회하여 출발허브ID, 도착허브ID 추출

		//TODO: 2-2.허브: 존재하는 허브 ID 확인

		//TODO: 3.배송 생성: shippingPostReqDto(수령인, 수령인 번호) 등
		Shipping shipping = Shipping.builder()
			.status(ShippingStatus.HUB_WAIT)
			// 2.추출 출발허브ID,도착허브ID 추가
			.departureHubId(UUID.randomUUID())
			.destinationHubId(UUID.randomUUID())
			// 1.주소,요청사항 추가
			.address("주문에서 message 받은 주소")
			.requestDetails("")
			.recipientName(shippingPostReqDto.getRecipientName())
			.recipientPhone(shippingPostReqDto.getRecipientPhone())
			.companyShippingManagerId(null)
		.build();

		// Shipping 엔티티를 먼저 저장합니다.
		Shipping savedShipping = shippingRepository.save(shipping);
		log.info("Creating shipping {}", savedShipping);

		//TODO: 4.배송 경로기록 생성: 허브간 이동정보 feign client 매개변수(출발/도착허브 ID)와 일치하는 예상거리, 소요시간, 경유지(List) 추출
		// 허브간 이동정보(하) 구현 시, 배송 허브 순번 1 고정
		// 허브간 이동정보(상) 구현 시, 경유지 List.length로 총 순번 지정
		ShippingHistory shippingHistory = ShippingHistory.builder()
			.shipping(savedShipping)
			.shippingPathSequence(1)
			.departureHubId(shipping.getDepartureHubId())
			.destinationHubId(shipping.getDestinationHubId())
			.shippingManagerId(UUID.randomUUID()) // 배송담당자: type(허브담당자 10명) 중, 한명 라운드 로빈 배정
			.estiDist(123432.23)
			.estTime(132421)
			.actDist(123445.34)
			.actTime(132421)
			.historyStatus(ShippingHistoryStatus.HUB_WAIT)
		.build();

		//savedShipping.addShippingHistory(shippingHistory);
		ShippingHistory savedShippingHistory = shippingHistoryRepository.save(shippingHistory);
		log.info("savedShippingHistory 상태 {}", savedShippingHistory.getHistoryStatus());


		//TODO: 5.주문: 전달 messageDto(배송ID, 예외상태 코드)

		//TODO: 6.주문이 생성되면 orderId과 메시지 전송시간을 전달 받은 후, 배송 orderId UPDATE
		// 주문 생성 전까지 허브간 이동 불가 = 배송 경로기록 상태(HUB_WAIT) 변경 못함.

		//TODO: 7.배송 담당자 배정 처리(주문 생성 전송시간 기준으로 허브간 이동이 시작되었다고 가정)
		// 전송시간+예상소요시간 기준
		// 배송 경로기록 마지막 순번의 현재상태가 "목적지 허브 도착:HUB_ARV"일 때만 가능
		// 배송 진행여부 확인해서 "대기 중:False"일 때만 라운드 로빈 배정

		//TODO: 8.배송 업체배송담당자ID, 현재상태(HUB_ARV) update

		return savedShipping.toPostResponseDto();
	}
}
