package com.davcatch.devcatch.common.service.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.davcatch.devcatch.common.config.CacheConfig;
import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;
import com.davcatch.devcatch.common.service.mail.VerificationInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerifyCodeCacheService {


	/**
	 * 이메일 인증 캐시 저장
	 * @param verifyCode Key
	 * @param verificationInfo Value
	 * @return 캐시에 저장된 VerificationInfo
	 */
	@CachePut(
		value = CacheConfig.EMAIL_VERIFICATION_CACHE,
		key = "#verifyCode"
	)
	public VerificationInfo putVerificationCode(String verifyCode, VerificationInfo verificationInfo) {
		log.debug("이메일 {} 인증 코드 Cache 저장", verificationInfo.getEmail());
		return verificationInfo;
	}

	/**
	 * 캐시 조회
	 * @param verifyCode key
	 * @return VerificationInfo.class
	 * @throws CustomException 인증코드가 맞지 않음 (캐시에 key 없음)
	 */
	@Cacheable(
		value = CacheConfig.EMAIL_VERIFICATION_CACHE,
		key = "#verifyCode",
		unless = "#result == null"
	)
	public VerificationInfo getVerificationCode(String verifyCode) throws CustomException {
		// 캐시 Miss가 발생한 경우 (직접 확인)
		log.warn("유효하지 않은 인증 코드 시도: {}", verifyCode);
		throw new CustomException(ErrorCode.VERIFY_CODE_WRONG);
	}

	/**
	 * Key에 해당하는 캐시 제거
	 * @param verifyCode Key
	 */
	@CacheEvict(
		value = CacheConfig.EMAIL_VERIFICATION_CACHE,
		key = "#verifyCode"
	)
	public void evictVerificationCode(String verifyCode) {
		log.debug("인증 코드 {} 캐시에서 제거", verifyCode);
	}
}
