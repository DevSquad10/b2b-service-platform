package com.devsquad10.shipping.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hub", url = "http://localhost:19094/api/hub")
public interface HubServiceClient {

	@GetMapping("/exists/{uuid}")
	ResponseEntity<String> isHubExists(@PathVariable(name = "uuid") UUID uuid);
}
