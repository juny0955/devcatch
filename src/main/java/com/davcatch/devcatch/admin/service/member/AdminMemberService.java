package com.davcatch.devcatch.admin.service.member;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.davcatch.devcatch.domain.member.Member;
import com.davcatch.devcatch.repository.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberService {

	private final MemberRepository memberRepository;

	public List<Member> getDashBoardMemberList() {
		return memberRepository.findDashBoardList(PageRequest.of(0, 5));
	}

	public int getTotalMemberSize() {
		return memberRepository.findTotalMemberSize();
	}
}
