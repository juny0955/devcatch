package com.davcatch.devcatch.admin.service.mail;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import com.davcatch.devcatch.admin.controller.mail.request.SendMailReqeust;
import com.davcatch.devcatch.common.service.mail.MailService;
import com.davcatch.devcatch.common.service.mail.MailTemplate;
import com.davcatch.devcatch.common.util.MailUtil;
import com.davcatch.devcatch.domain.member.Member;
import com.davcatch.devcatch.repository.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMailService {

	private final MemberRepository memberRepository;
	private final MailService mailService;

	public void sendMailToAllMember(SendMailReqeust reqeust) {
		List<Member> members = memberRepository.findAll();

		Context context = MailUtil.createAdminContext(reqeust.getSubject(), reqeust.getContent());

		for (Member member : members) {
			mailService.sendMail(member.getEmail(), MailTemplate.ADMIN_MAIL, context);
		}
	}
}
