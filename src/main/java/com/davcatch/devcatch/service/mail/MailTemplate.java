package com.davcatch.devcatch.service.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MailTemplate {

	VERIFY_TITLE("[데브캐치] 이메일 인증코드", "mail/verifyCode.html"),
	SEND_ARTICLE("[데브캐치] 새로운 아티클이 등록되었어요!", "mail/newArticle.html"),
	ADMIN_MAIL("[데브캐치] 관리자에게 메일이 도착했습니다", "mail/adminMail.html")
	;

	private final String title;
	private final String templatePath;
}
