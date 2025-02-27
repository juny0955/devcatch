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

	/**
	 * 캐시 저장
	 * @param verifyCode Key
	 * @param verificationInfo Value
	 */
	public void putVerificationCode(String verifyCode, VerificationInfo verificationInfo) {
		Cache cache = cacheManager.getCache(CACHE_NAME);
		if (cache != null)
			cache.put(verifyCode, verificationInfo);
	}

	/**
	 * 캐시 조회
	 * @param verifyCode key
	 * @return VerificationInfo.class
	 * @throws CustomException 인증코드가 맞지 않음 (캐시에 key 없음)
	 */
	public VerificationInfo getVerificationCode(String verifyCode) throws CustomException {
		Cache cache = cacheManager.getCache(CACHE_NAME);
		if (cache != null) {
			VerificationInfo verificationInfo = cache.get(verifyCode, VerificationInfo.class);
			if (verificationInfo == null)
				throw new CustomException(ErrorCode.VERIFY_CODE_WRONG);

			return verificationInfo;
		}

		return null;
	}

	/**
	 * Key에 해당하는 캐시 제거
	 * @param verifyCode Key
	 */
	public void evictVerificationCode(String verifyCode) {
		Cache cache = cacheManager.getCache(CACHE_NAME);
		if (cache != null)
			cache.evict(verifyCode);
	}
}
