package com.davcatch.devcatch.service.reg;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.davcatch.devcatch.controller.RegRequest;
import com.davcatch.devcatch.domain.Member;
import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.exception.ErrorCode;
import com.davcatch.devcatch.service.mail.MailService;
import com.davcatch.devcatch.service.mail.MailTemplate;
import com.davcatch.devcatch.service.mail.VerificationInfo;
import com.davcatch.devcatch.service.mail.VerifyCodeCacheService;
import com.davcatch.devcatch.service.member.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RegService {

	private final MemberService memberService;
	private final MailService mailService;
	private final VerifyCodeCacheService verifyCodeCacheService;

	/**
	 * 가입전 인증 진행
	 *
	 * 이메일 중복 체크 ->
	 * 인증 코드 생성 및 캐시 저장 ->
	 * 메일 발송
	 * @param request 가입 정보
	 * @throws CustomException 메일 중복 예외
	 */
	public void verify(RegRequest request) throws CustomException {
		if (!memberService.existsEmailCheck(request.getEmail()))
			throw new CustomException(ErrorCode.EXISTS_EMAIL);

		VerificationInfo verificationInfo = VerificationInfo.create(request.getName());
		verifyCodeCacheService.putVerificationCode(request.getEmail(), verificationInfo);

		mailService.sendMail(request.getEmail(), MailTemplate.VERIFY_TITLE);
	}

	/** 신규 가입 진행
	 *
	 * email 파라미터 값으로 캐시에서 정보를 가져온뒤 가입 진행
	 * 인증번호 체크 -> 만료시간 체크 후 통과시 DB 저장 진행
	 * @param email 가입할 이메일
	 * @throws CustomException 이메일 인증시간 초과 예외
	 * @throws CustomException 잘못된 인증코드 예외
	 */
	@Transactional
	public void register(String email, String verifyCode) throws CustomException {
		VerificationInfo verificationInfo = verifyCodeCacheService.getVerificationCode(email);

		if (verifyCode.equals(verificationInfo.getVerifyCode())) {
			if (verificationInfo.isExpired()) {
				log.info("이메일 인증 중 시간 만료 : {}", email);
				verifyCodeCacheService.evictVerificationCode(email);
				throw new CustomException(ErrorCode.VERIFY_CODE_EXPIRED);
			}
			log.debug("잘못된 인증코드 전달: {}", email);
			throw new CustomException(ErrorCode.VERIFY_CODE_WRONG);
		}

		memberService.save(Member.of(verificationInfo.getName(), email));
	}
}
