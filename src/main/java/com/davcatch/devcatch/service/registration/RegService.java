package com.davcatch.devcatch.service.registration;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import com.davcatch.devcatch.controller.registration.RegRequest;
import com.davcatch.devcatch.domain.Member;
import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.exception.ErrorCode;
import com.davcatch.devcatch.service.mail.MailService;
import com.davcatch.devcatch.service.mail.MailTemplate;
import com.davcatch.devcatch.service.mail.VerificationInfo;
import com.davcatch.devcatch.service.cache.VerifyCodeCacheService;
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
	 * 1. 이메일 중복 체크
	 * 2. 인증 코드 생성 및 캐시 저장
	 * 3. 메일 발송
	 * @param request 가입 정보
	 * @throws CustomException 메일 중복 예외
	 */
	public void verify(RegRequest request) throws CustomException {
		if (!memberService.existsEmailCheck(request.getEmail()))
			throw new CustomException(ErrorCode.EXISTS_EMAIL);

		VerificationInfo verificationInfo = VerificationInfo.create(request);
		String verifyCode = UUID.randomUUID().toString().substring(0, 7);
		verifyCodeCacheService.putVerificationCode(verifyCode, verificationInfo);

		Context context = new Context();
		context.setVariable("subject", MailTemplate.VERIFY_TITLE.getTitle());
		context.setVariable("verifyCode", verifyCode);
		mailService.sendMail(request.getEmail(), MailTemplate.VERIFY_TITLE, context);
	}

	/** 신규 가입 진행
	 *
	 * verifyCode 파라미터 값으로 캐시에서 정보를 가져온뒤 가입 진행
	 * @param verifyCode 인증코드
	 * @throws CustomException 이메일 인증시간 초과 예외
	 * @throws CustomException 잘못된 인증코드 예외
	 */
	@Transactional
	public void register(String verifyCode) throws CustomException {
		VerificationInfo verificationInfo = verifyCodeCacheService.getVerificationCode(verifyCode);

		if (verificationInfo.isExpired()) {
			log.info("이메일 인증 중 시간 만료 : {}", verificationInfo.getEmail());
			verifyCodeCacheService.evictVerificationCode(verifyCode);
			throw new CustomException(ErrorCode.VERIFY_CODE_EXPIRED);
		}

		memberService.save(Member.from(verificationInfo));
	}
}
