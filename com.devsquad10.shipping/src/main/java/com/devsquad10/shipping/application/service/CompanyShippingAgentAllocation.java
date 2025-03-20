package com.devsquad10.shipping.application.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsquad10.shipping.application.dto.MinimumCountAllocationResult;
import com.devsquad10.shipping.application.exception.shippingAgent.ShippingAgentAlreadyAllocatedException;
import com.devsquad10.shipping.application.exception.shippingAgent.ShippingAssignmentCountException;
import com.devsquad10.shipping.application.exception.shippingAgent.ShippingStatusIsNotAllocatedException;
import com.devsquad10.shipping.application.exception.shippingAgent.SlackMessageSendToDesHubManagerIdException;
import com.devsquad10.shipping.domain.enums.ShippingAgentType;
import com.devsquad10.shipping.domain.enums.ShippingStatus;
import com.devsquad10.shipping.domain.model.ShippingAgent;
import com.devsquad10.shipping.domain.repository.ShippingAgentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyShippingAgentAllocation implements ShippingAgentAllocationMethod {

	private final ShippingAgentRepository shippingAgentRepository;

	@Override
	@Transactional
	public MinimumCountAllocationResult allocationResult(UUID companyShippingManagerId, UUID destinationHubId, ShippingStatus shippingStatus) {

		// 배송담당자 ID가 이미 배정된 경우 처리
		if (companyShippingManagerId != null) {
			throw new ShippingAgentAlreadyAllocatedException("업체 배송담당자가 이미 배정되어 담당자배정 불가합니다.");
		}

		// 목적지 허브 도착전까지 업체배달 담당자 할당 불가
		if(shippingStatus != ShippingStatus.HUB_ARV) {
			throw new ShippingStatusIsNotAllocatedException(shippingStatus + " 상태로 담당자 배정 불가합니다.");
		}

		// 배송 1건당 모든 배송담당자 List 중 1명 배정
		List<ShippingAgent> shippingAgentList = shippingAgentRepository.findAllByDeletedAtIsNull();
		if(shippingAgentList == null) {
			throw new IllegalArgumentException("배송담당자가 존재하지 않습니다.");
		}

		// 1.업체담당자만 할당
		// 2.isTransit == false(배송 가능)
		// 3.도착허브 ID와 소속허브 ID가 일치(해당 소속허브 담당자만 업체배송 가능)
		List<ShippingAgent> possibleShippingAgents = shippingAgentList.stream()
			.filter(agent -> agent.getType() == ShippingAgentType.COM_DVL
				&& !agent.getIsTransit()
				&& agent.getHubId().equals(destinationHubId))
			.toList();
		log.info("possibleShippingAgents.size() = {}" , possibleShippingAgents.size());

		// 모든 업체배송 담당자가 배송 불가능한 경우
		if(possibleShippingAgents.isEmpty()) {
			// TODO: 도착허브 ID의 담당자에게 슬랙 메시지 전송
			throw new  SlackMessageSendToDesHubManagerIdException(destinationHubId + " 허브의 가능한 업체배송담당자가 존재하지 않습니다.");
		}

		log.info("possibleShippingAgents.size() = {}" , possibleShippingAgents.size());
		// 최소 배정 건수 담당자 선택
		ShippingAgent selectedAgent = possibleShippingAgents.stream()
			.min(Comparator.comparingInt(ShippingAgent::getAssignmentCount))
			.orElse(null);

		// 배정 횟수 증가 및 저장, 배송 진행 여부 수정 및 저장
		try {
			// 선택된 담당자 배타적 락 획득
			ShippingAgent lockedAgent = shippingAgentRepository.findByIdWithPessimisticLock(selectedAgent.getId());
			lockedAgent.increaseAssignmentCount();
			lockedAgent.updateIsTransit();
			shippingAgentRepository.save(lockedAgent);

			return MinimumCountAllocationResult.builder()
				.hubId(selectedAgent.getHubId())
				.shippingManagerId(lockedAgent.getShippingManagerId())
				.shippingManagerSlackId(lockedAgent.getShippingManagerSlackId())
				.shippingSequence(lockedAgent.getShippingSequence())
				.isTransit(lockedAgent.getIsTransit())
				.assignmentCount(lockedAgent.getAssignmentCount())
				.build();

		} catch (DataAccessException e) {
			throw new ShippingAssignmentCountException("배정 횟수 변경에 문제가 발생하였습니다.", e);
		}
	};
}
