package com.devsquad10.gateway.infrastructure.filter;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LocalJwtAuthenticationFilter implements GlobalFilter {

	@Value("${service.jwt.secret-key}")
	private String secretKey;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String path = exchange.getRequest().getURI().getPath();

		if (path.equals("/api/user/signIn") || path.equals("/api/user/signup")) {
			return chain.filter(exchange);
		}

		String token = extractToken(exchange);

		if (token == null || !validateToken(token)) {
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		return chain.filter(exchange);
	}

	private String extractToken(ServerWebExchange exchange) {
		String token = exchange.getRequest().getHeaders().getFirst("Authorization");
		if (token != null && token.startsWith("Bearer ")) {
			return token.substring(7);
		}
		return null;
	}

	private boolean validateToken(String token) {
		try {
			SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
			Jws<Claims> claimsJws = Jwts.parser()
				.verifyWith(key)
				.build().parseSignedClaims(token);
			log.info("######payload :: {}", claimsJws.getPayload().toString());

			if (claimsJws.getPayload().getExpiration().before(new Date())) {
				throw new Exception("Token expired");
			}

			return true;
		} catch (Exception e) {
			log.error("Token validation failed: {}", e.getMessage());
			return false;
		}
	}
}
