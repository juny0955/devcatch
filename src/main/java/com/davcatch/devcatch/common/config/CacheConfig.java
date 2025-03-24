package com.davcatch.devcatch.common.config;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CacheConfig {

	public static final String EMAIL_VERIFICATION_CACHE = "emailVerification";

	@Bean
	public CacheManager cacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager();

		cacheManager.setCacheNames(List.of(EMAIL_VERIFICATION_CACHE));

		cacheManager.setCaffeine(
			Caffeine.newBuilder()
				.expireAfterWrite(5, TimeUnit.MINUTES)
				.initialCapacity(100)
				.maximumSize(1000)
				.recordStats()
		);

		return cacheManager;
	}

}
