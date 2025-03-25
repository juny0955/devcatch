package com.davcatch.devcatch.common.service.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MailTemplate {

	VERIFY_EMAIL("[데브캐치] 이메일 인증코드", "mail/verifyCode.html"),
	FIND_PASSWORD("[데브캐치] 임시 비밀번호를 보내드립니다", "mail/tempPassword.html"),
	SEND_ARTICLE("[데브캐치] 새로운 아티클이 등록되었어요!", "mail/newArticle.html"),
	ADMIN_MAIL("[데브캐치] 관리자에게 메일이 도착했습니다", "mail/adminMail.html")
	;

	private final String title;
	private final String templatePath;
}
