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

import com.devsquad10.company.application.client.HubClient;
import com.devsquad10.company.application.dto.CompanyReqDto;
import com.devsquad10.company.application.dto.CompanyResDto;
import com.devsquad10.company.application.exception.CompanyNotFoundException;
import com.devsquad10.company.domain.enums.CompanyTypes;
import com.devsquad10.company.domain.model.Company;
import com.devsquad10.company.domain.repository.CompanyRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {

	private final CompanyRepository companyRepository;
	private final HubClient hubClient;

	@CachePut(cacheNames = "companyCache", key = "#result.id")
	public CompanyResDto createCompany(CompanyReqDto companyReqDto) {

		UUID hubId = companyReqDto.getHubId();

		//1. 허브 존재 유무 확인
		if (!hubClient.isHubExists(hubId)) {
			throw new EntityNotFoundException("Hub Not Found By Id : " + hubId);
		}

		// 담당자 id는 유저 완성 시 등록
		return companyRepository.save(Company.builder()
			.name(companyReqDto.getName())
			.hubId(hubId)
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

	@Cacheable(cacheNames = "companySearchCache", key = "#q + '-' + #category + '-' + #page + '-' + #size")
	@Transactional(readOnly = true)
	public Page<CompanyResDto> searchCompanies(String q, String category, int page, int size, String sort,
		String order) {

		Page<Company> companyPages = companyRepository.findAll(q, category, page, size, sort, order);

		return companyPages.map(Company::toResponseDto);

	}

	@CachePut(cacheNames = "companyCache", key = "#id")
	@Caching(evict = {
		@CacheEvict(cacheNames = "companySearchCache", allEntries = true)
	})
	public CompanyResDto updateCompany(UUID id, CompanyReqDto companyReqDto) {
		Company targetCompany = companyRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new CompanyNotFoundException("Company Not Found By  Id : " + id));

		UUID hubId = companyReqDto.getHubId();

		//1. 허브 존재 유무 확인
		if (!hubClient.isHubExists(hubId)) {
			throw new EntityNotFoundException("Hub Not Found By Id : " + hubId);
		}

		return companyRepository.save(targetCompany.toBuilder()
			.name(companyReqDto.getName())
			.hubId(hubId)
			.address(companyReqDto.getAddress())
			.type(companyReqDto.getType())
			.updatedAt(LocalDateTime.now())
			.updatedBy("사용자")
			.build()).toResponseDto();
	}

	@Caching(evict = {
		@CacheEvict(cacheNames = "companyCache", key = "#id"),
		@CacheEvict(cacheNames = "companySearchCache", key = "#id")
	})
	public void deleteCompany(UUID id) {
		Company targetCompany = companyRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new CompanyNotFoundException("Company Not Found By  Id : " + id));

		companyRepository.save(targetCompany.toBuilder()
			.deletedAt(LocalDateTime.now())
			.deletedBy("사용자")
			.build());
	}

	public UUID getHubIdIfCompanyExists(UUID id) {
		Company company = companyRepository.findByIdAndDeletedAtIsNull(id)
			.orElse(null);
		return (company != null && company.getType().equals(CompanyTypes.SUPPLIER)) ? company.getHubId() : null;
	}

	public String getCompanyAddress(UUID id) {
		Company company = companyRepository.findByIdAndDeletedAtIsNull(id)
			.orElse(null);
		return (company != null && company.getType().equals(CompanyTypes.RECIPIENTS)) ? company.getAddress() : null;
	}
}
