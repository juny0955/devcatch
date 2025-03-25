package com.davcatch.devcatch.web.service.auth;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import com.davcatch.devcatch.web.controller.auth.request.RegRequest;
import com.davcatch.devcatch.domain.member.Member;
import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;
import com.davcatch.devcatch.common.service.cache.VerifyCodeCacheService;
import com.davcatch.devcatch.common.service.mail.MailService;
import com.davcatch.devcatch.common.service.mail.MailTemplate;
import com.davcatch.devcatch.common.service.mail.VerificationInfo;
import com.davcatch.devcatch.web.service.member.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

	private final MemberService memberService;
	private final MailService mailService;
	private final VerifyCodeCacheService verifyCodeCacheService;
	private final PasswordEncoder passwordEncoder;
	private final SecureRandom secureRandom = new SecureRandom();

	/**
	 * 가입전 인증 진행
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
		String verifyCode = generateVerificationCode();
		verifyCodeCacheService.putVerificationCode(verifyCode, verificationInfo);

		Context context = new Context();
		context.setVariable("subject", MailTemplate.VERIFY_EMAIL.getTitle());
		context.setVariable("verifyCode", verifyCode);

		mailService.sendMail(request.getEmail(), MailTemplate.VERIFY_EMAIL, context);
	}

	/** 신규 가입 진행
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

		String encodedPassword = passwordEncoder.encode(verificationInfo.getPassword());
		memberService.save(Member.from(verificationInfo, encodedPassword));
	}

	@Transactional
	public void leave(String email) {
		log.info("회원 {} 구독 취소", email);

		memberService.remove(email);
	}

	@Transactional
	public void findPassword(String email) throws CustomException {
		log.info("비밀번호 재설정 요청: {}", email);
		Member member = memberService.getMemberByEamil(email);

		String tempPassword = generateTempPassword();
		String encodedPassword = passwordEncoder.encode(tempPassword);
		member.changePassword(encodedPassword);

		Context context = new Context();
		context.setVariable("subject", MailTemplate.FIND_PASSWORD.getTitle());
		context.setVariable("name", member.getName());
		context.setVariable("tempPassword", tempPassword);

		mailService.sendMail(email, MailTemplate.FIND_PASSWORD, context);
		log.info("회원 {} 임시 비밀번호 발급", member.getName());
	}

	public String generateVerificationCode() {
		byte[] randomBytes = new byte[6];
		secureRandom.nextBytes(randomBytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes).substring(0, 8);
	}

	private String generateTempPassword() {
		byte[] randomBytes = new byte[12];
		secureRandom.nextBytes(randomBytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes).substring(0, 12);
	}
}
