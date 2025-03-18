package com.devsquad10.shipping.application.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsquad10.shipping.application.exception.HubIdNotFoundException;
import com.devsquad10.shipping.application.exception.ShippingAgentTypeNotFoundException;
import com.devsquad10.shipping.domain.enums.ShippingAgentType;
import com.devsquad10.shipping.domain.model.ShippingAgent;
import com.devsquad10.shipping.domain.repository.ShippingAgentRepository;
import com.devsquad10.shipping.infrastructure.client.HubServiceClient;
import com.devsquad10.shipping.infrastructure.client.ShippingAgentPostFeignRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShippingAgentService {

	private final HubServiceClient hubServiceClient;
	private final ShippingAgentRepository shippingAgentRepository;
	private final ObjectMapper objectMapper;

	public void createShippingAgent(@Valid ShippingAgentPostFeignRequest request) {

		// TODO: User 정보 feign client 로 받기
		UUID reqShippingManagerId = request.getId(); // 배송담당자 ID
		String reqSlackId = request.getSlackId();

		// 담당자 타입 존재 유효성 검사
		ShippingAgentType reqType = request.getType();
		if(reqType != ShippingAgentType.HUB_DVL && reqType != ShippingAgentType.COM_DVL) {
			throw new ShippingAgentTypeNotFoundException(reqType + " Shipping Agent type is not supported");
		}

		// HubId 존재 유효성 검사
		UUID reqHubId = request.getHubId();
		// HubId 존재 유무 feign client 호출
		ResponseEntity<String> response = hubServiceClient.isHubExists(reqHubId);
		if (response != null && response.getBody() != null) {
			JsonNode root = null;
			try {
				root = objectMapper.readTree(response.getBody());
				boolean bodyValue = root.path("body").asBoolean();
				if (!bodyValue) {
					// body 값이 false 경우 처리
					throw new HubIdNotFoundException("Hub id " + reqHubId + " not found");
				}
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new EntityNotFoundException("Hub id " + reqHubId + " not found");
		}

		// 순차적 순번 배정을 위한 최대값 추출 및 다음 순번 처리
		Optional<Integer> maxSequence = shippingAgentRepository.findMaxShippingSequence();
		Integer nextSequence = maxSequence.map(s->++s).orElse(1);
		log.info("nextSequence : {}", nextSequence);

		shippingAgentRepository.save(ShippingAgent.builder()
			.shippingManagerId(reqShippingManagerId)
			.hubId(reqHubId)
			.shippingManagerSlackId(reqSlackId)
			.type(request.getType())
			.shippingSequence(nextSequence)
			.isTransit(false)
			.build()
		);
	}
}
