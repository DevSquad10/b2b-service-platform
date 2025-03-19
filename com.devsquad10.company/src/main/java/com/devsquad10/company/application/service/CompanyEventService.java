package com.devsquad10.company.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsquad10.company.application.dto.message.StockSoldOutMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyEventService {

	public void stockSoldMessageSend(StockSoldOutMessage stockSoldOutMessage) {

		// 슬랙 메시지 전송
		System.out.println("stockSoldOutMessage.getProductId() = " + stockSoldOutMessage.getProductId());
		System.out.println("stockSoldOutMessage.getSoldOutAt() = " + stockSoldOutMessage.getSoldOutAt());
	}
}
