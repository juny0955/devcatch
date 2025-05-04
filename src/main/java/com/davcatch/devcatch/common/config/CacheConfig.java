package com.davcatch.devcatch.common.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

	public static final String EMAIL_VERIFICATION_CACHE = "emailVerification";
	public static final String LAST_ARTICLE_CACHE       = "lastArticle";

	private static final Map<String, Duration> TTL_MAP = new HashMap<>();
	static {
		TTL_MAP.put(EMAIL_VERIFICATION_CACHE, Duration.ofMinutes(5));
		TTL_MAP.put(LAST_ARTICLE_CACHE, null);
	}

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
			.disableCachingNullValues();

		RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(connectionFactory)
			.cacheDefaults(defaultConfig);

		for (String cacheName : TTL_MAP.keySet()) {
			RedisCacheConfiguration config = defaultConfig;
			Duration ttl = TTL_MAP.get(cacheName);
			if (ttl != null) {
				config = config.entryTtl(ttl);
			}
			builder.withCacheConfiguration(cacheName, config);
		}

		return builder.build();
	}
}
