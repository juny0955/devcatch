package com.davcatch.devcatch.service.mail;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerifyCodeCacheService {

	private static final String CACHE_NAME = "emailVerification";

	private final CacheManager cacheManager;

	public void putVerificationCode(String email, VerificationInfo verificationInfo) {
		Cache cache = cacheManager.getCache(CACHE_NAME);
		if (cache != null)
			cache.put(email, verificationInfo);
	}

	public VerificationInfo getVerificationCode(String email) {
		Cache cache = cacheManager.getCache(CACHE_NAME);
		if (cache != null)
			return cache.get(email, VerificationInfo.class);

		return null;
	}

	public void evictVerificationCode(String email) {
		Cache cache = cacheManager.getCache(CACHE_NAME);
		if (cache != null)
			cache.evict(email);
	}
}
