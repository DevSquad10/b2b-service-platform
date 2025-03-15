package com.devsquad10.company.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsquad10.company.application.dto.CompanyReqDto;
import com.devsquad10.company.application.service.CompanyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company")
public class CompanyController {

	private final CompanyService companyService;

	@PostMapping
	public ResponseEntity<?> createCompany(@RequestBody CompanyReqDto companyReqDto) {

		companyService.createCompany(companyReqDto);

		return ResponseEntity.status(HttpStatus.OK)
			.body("Company successfully registered");

	}

}
