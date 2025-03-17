package com.devsquad10.company.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
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

	@CachePut(cacheNames = "companyCache", key = "#result.id")
	public CompanyResDto createCompany(CompanyReqDto companyReqDto) {

		//1. 허브가 존재 유무 확인

		//2. 구현
		//추후 허브 id , 매니저 id 추가
		return companyRepository.save(Company.builder()
			.name(companyReqDto.getName())
			.address(companyReqDto.getAddress())
			.type(companyReqDto.getType())
			.build()).toResponseDto();
	}

	@Cacheable(cacheNames = "companyCache", key = "#id")
	@Transactional(readOnly = true)
	public CompanyResDto getCompanyById(UUID id) {
		return companyRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new CompanyNotFoundException("Company Not Found By  Id : " + id))
			.toResponseDto();
	}

	@Cacheable(cacheNames = "companySearch", key = "#q + '-' + #category + '-' + #page + '-' + #size")
	@Transactional(readOnly = true)
	public Page<CompanyResDto> searchCompanies(String q, String category, int page, int size, String sort,
		String order) {

		Page<Company> companyPages = companyRepository.findAll(q, category, page, size, sort, order);

		return companyPages.map(Company::toResponseDto);

	}

	@CachePut(cacheNames = "companyCache", key = "#id")
	@Caching(evict = {
		@CacheEvict(cacheNames = "companySearch", allEntries = true)
	})
	public CompanyResDto updateCompany(UUID id, CompanyReqDto companyReqDto) {
		Company targetCompany = companyRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new CompanyNotFoundException("Company Not Found By  Id : " + id));

		return companyRepository.save(targetCompany.toBuilder()
			.name(companyReqDto.getName())
			.venderId(companyReqDto.getVenderId())
			.hubId(companyReqDto.getHubId())
			.address(companyReqDto.getAddress())
			.type(companyReqDto.getType())
			.updatedAt(LocalDateTime.now())
			.updatedBy("사용자")
			.build()).toResponseDto();
	}

	@Caching(evict = {
		@CacheEvict(cacheNames = "companyCache", key = "#id"),
		@CacheEvict(cacheNames = "companySearch", key = "#id")
	})
	public void deleteCompany(UUID id) {
		Company targetCompany = companyRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new CompanyNotFoundException("Company Not Found By  Id : " + id));

		companyRepository.save(targetCompany.toBuilder()
			.deletedAt(LocalDateTime.now())
			.deletedBy("사용자")
			.build());
	}
}
