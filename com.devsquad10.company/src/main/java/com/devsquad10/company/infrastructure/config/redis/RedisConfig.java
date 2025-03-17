package com.devsquad10.company.infrastructure.config.redis;

import java.time.Duration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

	public static final String COMPANY_CACHE = "companyCache";
	public static final String COMPANY_SEARCH_CACHE = "companySearchCache";

	private static final Duration DEFAULT_TTL = Duration.ofSeconds(120);
	private static final Duration COMPANY_TTL = Duration.ofMinutes(10);
	private static final Duration COMPANY_SEARCH_TTL = Duration.ofHours(1);

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		// 이진 직렬화 방식 적용
		JdkSerializationRedisSerializer redisSerializer = new JdkSerializationRedisSerializer();

		// 기본값 캐시 설정 (2분 TTL, JSON 직렬화 방식)
		RedisCacheConfiguration defaultConfiguration = RedisCacheConfiguration
			.defaultCacheConfig()
			.disableCachingNullValues()
			.entryTtl(DEFAULT_TTL) // 기본 유지 시간 120초
			.computePrefixWith(CacheKeyPrefix.simple())
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer));

		// companyCache 에 대한 캐시 설정 (10분 TTL, JSON 직렬화 방식)
		RedisCacheConfiguration companyConfiguration = RedisCacheConfiguration
			.defaultCacheConfig()
			.disableCachingNullValues()
			.entryTtl(COMPANY_TTL)
			.computePrefixWith(CacheKeyPrefix.simple())
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer));

		// companySearchCache 에 대한 캐시 설정 (1시간 TTL, JSON 직렬화 방식)
		RedisCacheConfiguration companySearchConfiguration = RedisCacheConfiguration
			.defaultCacheConfig()
			.disableCachingNullValues()
			.entryTtl(COMPANY_SEARCH_TTL)
			.computePrefixWith(CacheKeyPrefix.simple())
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer));

		return RedisCacheManager
			.builder(redisConnectionFactory)
			.cacheDefaults(defaultConfiguration)
			.withCacheConfiguration(COMPANY_CACHE, companyConfiguration)
			.withCacheConfiguration(COMPANY_SEARCH_CACHE, companySearchConfiguration)
			.build();
	}
}
