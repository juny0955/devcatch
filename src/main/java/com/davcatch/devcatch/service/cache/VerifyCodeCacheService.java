package com.davcatch.devcatch.service.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.exception.ErrorCode;
import com.davcatch.devcatch.service.mail.VerificationInfo;

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

	public VerificationInfo getVerificationCode(String email) throws CustomException {
		Cache cache = cacheManager.getCache(CACHE_NAME);
		if (cache != null) {
			VerificationInfo verificationInfo = cache.get(email, VerificationInfo.class);
			if (verificationInfo == null)
				throw new CustomException(ErrorCode.VERIFY_CODE_WRONG);

			return verificationInfo;
		}

		return null;
	}

	public void evictVerificationCode(String email) {
		Cache cache = cacheManager.getCache(CACHE_NAME);
		if (cache != null)
			cache.evict(email);
	}
}
