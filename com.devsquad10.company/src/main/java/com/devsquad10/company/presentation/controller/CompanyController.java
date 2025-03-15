package com.devsquad10.company.presentation.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	@GetMapping("/{id}")
	public ResponseEntity<?> getCompanyById(@PathVariable("id") UUID id) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(companyService.getCompanyById(id));
	}

}
