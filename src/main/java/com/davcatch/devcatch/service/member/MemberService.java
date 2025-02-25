package com.davcatch.devcatch.service.member;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.davcatch.devcatch.domain.Member;
import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.exception.ErrorCode;
import com.davcatch.devcatch.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

	private final MemberRepository memberRepository;

	/**
	 * 이메일 중복 체크
	 * DB에 중복된 email 요청시 false
	 * DB에 중복되지 않은 email 요청시 true
	 *
	 * @param email 요청 Email
	 * @return 중복 여부
	 */
	public boolean existsEmailCheck(String email) {
		log.debug("이메일 중복 체크 시작");
		if (memberRepository.existsByEmail(email)) {
			log.info("중복된 이메일 등록 요청 : {}", email);
			return false;
		}

		log.debug("이메일 중복 체크 정상 종료");
		return true;
	}

	/**
	 * 회원 DB 저장
	 * @param member 저장할 회원
	 * @throws CustomException DB Insert 중 예외 발생
	 */
	@Transactional
	public void save(Member member) throws CustomException {
		try {
			memberRepository.save(member);
		} catch (Exception e) {
			log.error("신규 회원 {}, DB 저장 중 예외 발생 : {}", member.getEmail(), e.getMessage());
			throw new CustomException(ErrorCode.SERVER_ERROR);
		}

		log.info("회원 등록 : {} | {}", member.getName(), member.getEmail());
	}
}
