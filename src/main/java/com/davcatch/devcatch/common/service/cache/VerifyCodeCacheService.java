package com.davcatch.devcatch.common.service.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.davcatch.devcatch.common.config.CacheConfig;
import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;
import com.davcatch.devcatch.common.service.mail.VerificationInfo;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerifyCodeCacheService {

	private final CacheManager cacheManager;
	private Cache emailVerificationCache;

	@PostConstruct
	public void init() {
		emailVerificationCache = cacheManager.getCache(CacheConfig.EMAIL_VERIFICATION_CACHE);

		if (emailVerificationCache == null) {
			log.error("이메일 인증 캐시 '{}'를 찾을 수 없습니다", CacheConfig.EMAIL_VERIFICATION_CACHE);
			throw new IllegalStateException("이메일 인증 캐시 찾을수 없음, CacheConfig 확인");
		}

		log.debug("이메일 인증 캐시 '{}' 초기화 완료 (TTL 5분)", CacheConfig.EMAIL_VERIFICATION_CACHE);
	}

	/**
	 * 캐시 저장
	 * @param verifyCode Key
	 * @param verificationInfo Value
	 */
	public void putVerificationCode(String verifyCode, VerificationInfo verificationInfo) {
		emailVerificationCache.put(verifyCode, verificationInfo);
		log.debug("이메일 {} 인증 코드 Cache 저장", verificationInfo.getEmail());
	}

	/**
	 * 캐시 조회
	 * @param verifyCode key
	 * @return VerificationInfo.class
	 * @throws CustomException 인증코드가 맞지 않음 (캐시에 key 없음)
	 */
	public VerificationInfo getVerificationCode(String verifyCode) throws CustomException {
		VerificationInfo verificationInfo = emailVerificationCache.get(verifyCode, VerificationInfo.class);

		if (verificationInfo == null) {
			log.warn("유효하지 않은 인증 코드 시도: {}", verifyCode);
			throw new CustomException(ErrorCode.VERIFY_CODE_WRONG);
		}

		log.info("이메일 {} 인증코드 인증 성공", verificationInfo.getEmail());
		return verificationInfo;
	}

	/**
	 * Key에 해당하는 캐시 제거
	 * @param verifyCode Key
	 */
	public void evictVerificationCode(String verifyCode) {
		emailVerificationCache.evict(verifyCode);
		log.debug("인증 코드 {} 캐시에서 제거", verifyCode);
	}


}
