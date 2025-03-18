package com.devsquad10.product.infrastructure.config.redis;

import java.time.Duration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.devsquad10.product.application.dto.ProductResDto;

@Configuration
@EnableCaching
public class RedisCachingConfig {

	public static final String PRODUCT_CACHE = "productCache";
	public static final String PRODUCT_SEARCH_CACHE = "productSearchCache";

	private static final Duration DEFAULT_TTL = Duration.ofSeconds(120);
	private static final Duration PRODUCT_TTL = Duration.ofMinutes(10);
	private static final Duration PRODUCT_SEARCH_TTL = Duration.ofHours(1);

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

		Jackson2JsonRedisSerializer<ProductResDto> companySerializer = new Jackson2JsonRedisSerializer<>(
			ProductResDto.class);

		// 기본값 캐시 설정 (2분 TTL, JSON 직렬화 방식)
		RedisCacheConfiguration defaultConfiguration = RedisCacheConfiguration
			.defaultCacheConfig()
			.disableCachingNullValues()
			.entryTtl(DEFAULT_TTL) // 기본 유지 시간 120초
			.computePrefixWith(CacheKeyPrefix.simple())
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(companySerializer));

		// productCache 에 대한 캐시 설정 (10분 TTL, JSON 직렬화 방식)
		RedisCacheConfiguration productConfiguration = RedisCacheConfiguration
			.defaultCacheConfig()
			.disableCachingNullValues()
			.entryTtl(PRODUCT_TTL)
			.computePrefixWith(CacheKeyPrefix.simple())
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(companySerializer));

		// productSearchCache 에 대한 캐시 설정 (1시간 TTL, JSON 직렬화 방식)
		RedisCacheConfiguration productSearchConfiguration = RedisCacheConfiguration
			.defaultCacheConfig()
			.disableCachingNullValues()
			.entryTtl(PRODUCT_SEARCH_TTL)
			.computePrefixWith(CacheKeyPrefix.simple())
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(companySerializer));

		return RedisCacheManager
			.builder(redisConnectionFactory)
			.cacheDefaults(defaultConfiguration)
			.withCacheConfiguration(PRODUCT_CACHE, productConfiguration)
			.withCacheConfiguration(PRODUCT_SEARCH_CACHE, productSearchConfiguration)
			.build();
	}
}
