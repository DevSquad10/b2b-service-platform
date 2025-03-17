package com.devsquad10.hub.infrastructure.config;

import java.time.Duration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableCaching
public class RedisConfig {

	public static final String HUB_CACHE = "hubCache";
	public static final String HUB_SEARCH_CACHE = "hubSearchCache";

	private static final Duration DEFAULT_TTL = Duration.ofSeconds(120);
	private static final Duration HUB_TTL = Duration.ofHours(1);
	private static final Duration HUB_SEARCH_TTL = Duration.ofHours(1);

	@Bean
	public RedisCacheManager cacheManager(
		RedisConnectionFactory redisConnectionFactory
	) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

		RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(DEFAULT_TTL)
			.disableCachingNullValues()
			.serializeKeysWith(RedisSerializationContext.SerializationPair
				.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair
				.fromSerializer(jsonSerializer));

		RedisCacheConfiguration hubCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(HUB_TTL)
			.disableCachingNullValues()
			.computePrefixWith(CacheKeyPrefix.simple())
			.serializeValuesWith(RedisSerializationContext.SerializationPair
				.fromSerializer(RedisSerializer.java()));

		RedisCacheConfiguration hubSearchCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(HUB_SEARCH_TTL)
			.disableCachingNullValues()
			.serializeKeysWith(RedisSerializationContext.SerializationPair
				.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair
				.fromSerializer(RedisSerializer.java()));

		return RedisCacheManager.builder(redisConnectionFactory)
			.cacheDefaults(cacheConfiguration)
			.withCacheConfiguration(HUB_CACHE, hubCacheConfig)
			.withCacheConfiguration(HUB_SEARCH_CACHE, hubSearchCacheConfig)
			.build();
	}
}
