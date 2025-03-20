package com.devsquad10.gateway.infrastructure.filter;

import java.util.Date;
import java.util.Objects;

import javax.crypto.SecretKey;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
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

	//@Value("${service.jwt.secret-key}")
	private String secretKey = "80371f9129bc1ce5cf56a280623c6045411446332c45fb90dd85c2b5d85175a2cWxhbGZxanNnaGVrIUAjISFkamFmcG5mKEAjQEhGbmlqZm9AJA==";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String path = exchange.getRequest().getURI().getPath();

		// 로그인 & 회원가입 API는 토큰 검증 제외
		if (path.equals("/api/user/signIn") || path.equals("/api/user/signup")) {
			return chain.filter(exchange);
		}

		// JWT 토큰 추출
		String token = extractToken(exchange);

		// 토큰이 없거나 유효하지 않으면 401 응답 반환
		if (token == null || !validateToken(token)) {
			log.warn("Unauthorized request - Missing or invalid token");
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		// JWT 디코딩 후 사용자 정보 추출
		Claims claims = decodeToken(token);
		if (claims == null) {
			log.warn("Unauthorized request - Invalid JWT claims");
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		// 사용자 정보 헤더에 추가
		exchange = exchange.mutate()
			.request(exchange.getRequest().mutate()
				.header("X-User-Id", Objects.toString(claims.getSubject(), "")) // 🔹 subject에서 userId 가져오기
				.header("X-Slack-Id", Objects.toString(claims.get("slack_id"), ""))
				.header("X-User-Role", Objects.toString(claims.get("role"), ""))
				.build())
			.build();

		log.info("JWT Token Verified - UserId: {}, SlackId: {}, Role: {}",
			claims.getSubject(), // 🔹 userId 출력
			claims.get("slack_id"),
			claims.get("role"));

		return chain.filter(exchange);
	}

	private String extractToken(ServerWebExchange exchange) {
		String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
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
				.build()
				.parseSignedClaims(token);

			// 토큰 만료 여부 확인
			if (claimsJws.getPayload().getExpiration().before(new Date())) {
				log.warn("Token expired: {}", claimsJws.getPayload().getExpiration());
				return false;
			}

			log.info("JWT validation success");
			return true;
		} catch (Exception e) {
			log.error("JWT validation failed: {}", e.getMessage());
			return false;
		}
	}

	private Claims decodeToken(String token) {
		try {
			SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
			return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (Exception e) {
			log.error("Failed to decode JWT token: {}", e.getMessage());
			return null;
		}
	}
}
