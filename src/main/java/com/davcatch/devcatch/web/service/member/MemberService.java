package com.davcatch.devcatch.web.service.member;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.davcatch.devcatch.web.controller.member.request.ChangePasswordRequest;
import com.davcatch.devcatch.domain.member.Member;
import com.davcatch.devcatch.domain.member.MemberTag;
import com.davcatch.devcatch.domain.tag.Tag;
import com.davcatch.devcatch.domain.tag.TagType;
import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;
import com.davcatch.devcatch.repository.member.MemberRepository;
import com.davcatch.devcatch.web.controller.member.request.ChangeSubscribeReqeust;
import com.davcatch.devcatch.web.service.tag.TagService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

	private final MemberRepository memberRepository;
	private final TagService tagService;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 이메일 중복 체크
	 * DB에 중복된 email 요청시 false
	 * DB에 중복되지 않은 email 요청시 true
	 *
	 * @param email 요청 Email
	 * @return 중복 여부
	 */
	public boolean existsEmailCheck(String email) {
		if (memberRepository.existsByEmail(email)) {
			log.info("중복된 이메일 등록 요청 : {}", email);
			return false;
		}
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

		log.info("회원 등록 : (이름 : {} | 이메일 : {})", member.getName(), member.getEmail());
	}

	/**
	 * 회원 삭제
	 * @param email 삭제할 이메일
	 */
	@Transactional
	public void remove(String email) {
		memberRepository.deleteByEmail(email);
	}

	/**
	 * 전체회원 조회
	 * memberTags, tag 포함
	 * @return 전체 회원
	 */
	public List<Member> getAllMemberWithTag() {
		return memberRepository.findAllMemberWithTag();
	}

	/**
	 * 회원 조회
	 * memberTags, tag 포함
	 * @param memberId 조회할 회원 ID
	 * @return 회원
	 * @throws CustomException
	 */
	public Member getMemberWithTag(Long memberId) throws CustomException {
		return memberRepository.findMemberWithTag(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
	}

	/**
	 * 회원 구독 태그 변경
	 * @param memberId 변경할 회원 ID
	 * @param reqeust 구독 설정 Request
	 */
	@Transactional
	public void changeSubscribe(Long memberId, ChangeSubscribeReqeust reqeust) throws CustomException {
		Member member = memberRepository.findMemberWithTag(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		member.changeSubscription(reqeust.isSubscribeAll());
		member.changeSubscribeForeign(reqeust.isSubscribeForeign());
		member.clearMemberTags();

		if (!reqeust.isSubscribeAll()) {
			if (reqeust.getSelectedTags() == null || reqeust.getSelectedTags().isEmpty())
				throw new CustomException(ErrorCode.BAD_REQUEST);

			List<Tag> tags = tagService.getInTagTypes(reqeust.getSelectedTags());

			List<MemberTag> memberTags = tags.stream()
				.map(tag -> MemberTag.of(tag, member))
				.toList();

			member.addMemberTags(memberTags);
		}

		log.info("회원 {} 구독 설정 변경", memberId);
	}

	@Transactional
	public void changePassword(Long memberId, ChangePasswordRequest request) throws CustomException {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword()))
			throw new CustomException(ErrorCode.PASSWORD_IS_WRONG);

		if (!request.getNewPassword().equals(request.getConfirmNewPassword()))
			throw new CustomException(ErrorCode.BAD_REQUEST);

		String encodedPassword = passwordEncoder.encode(request.getNewPassword());
		member.changePassword(encodedPassword);

		log.info("회원 {} 비밀번호 재설정", memberId);
	}

	public Member getMemberByEamil(String email) throws CustomException {
		return memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
	}
}
