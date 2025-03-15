package com.devsquad10.company.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsquad10.company.application.dto.CompanyReqDto;
import com.devsquad10.company.application.dto.CompanyResDto;
import com.devsquad10.company.application.exception.CompanyNotFoundException;
import com.devsquad10.company.domain.model.Company;
import com.devsquad10.company.domain.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {

	private final CompanyRepository companyRepository;

	public void createCompany(CompanyReqDto companyReqDto) {

		//1. 허브가 존재 유무 확인

		//2. 구현
		//추후 허브 id , 매니저 id 추가
		companyRepository.save(Company.builder()
			.name(companyReqDto.getName())
			.address(companyReqDto.getAddress())
			.type(companyReqDto.getType())
			.build());
	}

	public CompanyResDto getCompanyById(UUID id) {
		return companyRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new CompanyNotFoundException("Company Not Found By  Id : " + id))
			.toResponseDto();
	}

}
