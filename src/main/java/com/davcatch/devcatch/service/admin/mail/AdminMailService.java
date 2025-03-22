package com.davcatch.devcatch.service.admin.mail;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import com.davcatch.devcatch.controller.admin.mail.request.SendMailReqeust;
import com.davcatch.devcatch.domain.member.Member;
import com.davcatch.devcatch.repository.member.MemberRepository;
import com.davcatch.devcatch.service.common.mail.MailService;
import com.davcatch.devcatch.service.common.mail.MailTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMailService {

	private final MemberRepository memberRepository;
	private final MailService mailService;

	public void sendMailToAllMember(SendMailReqeust reqeust) {
		List<Member> members = memberRepository.findAll();

		Context context = new Context();
		context.setVariable("subject", reqeust.getSubject());
		context.setVariable("content", reqeust.getContent());
		for (Member member : members) {
			mailService.sendMail(member.getEmail(), MailTemplate.ADMIN_MAIL, context);
		}
	}
}
