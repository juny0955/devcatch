package com.davcatch.devcatch.common.util;

import java.util.List;

import org.thymeleaf.context.Context;

import com.davcatch.devcatch.common.service.mail.MailTemplate;
import com.davcatch.devcatch.domain.article.Article;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MailUtil {

	/**
	 * 새로운 아티클 메일 Context 생성
	 * @param email 회원 이메일
	 * @param toSendArticles 전송할 아티클 리스트
	 * @return Context
	 */
	public static Context createNewArticleContext(String email, List<Article> toSendArticles) {
		Context context = new Context();
		context.setVariable("subject", MailTemplate.SEND_ARTICLE.getTitle());
		context.setVariable("email", email);
		context.setVariable("articles", toSendArticles);
		return context;
	}

	/**
	 * 인증코드 전송 메일 Context 생성
	 * @param verifyCode 인증코드
	 * @return Context
	 */
	public static Context createAuthContext(String verifyCode) {
		Context context = new Context();
		context.setVariable("subject", MailTemplate.VERIFY_EMAIL.getTitle());
		context.setVariable("verifyCode", verifyCode);
		return context;
	}

	/**
	 * 임시 비밀번호 메일 Context 생성
	 * @param name 회원 이름
	 * @param tempPassword 임시 비밀번호
	 * @return Context
	 */
	public static Context createFindPasswordContext(String name, String tempPassword) {
		Context context = new Context();
		context.setVariable("subject", MailTemplate.FIND_PASSWORD.getTitle());
		context.setVariable("name", name);
		context.setVariable("tempPassword", tempPassword);
		return context;
	}

	/**
	 * 관리자 전송 메일 Context 생성
	 * @param subject 제목
	 * @param content 내용
	 * @return Context
	 */
	public static Context createAdminContext(String subject, String content) {
		Context context = new Context();
		context.setVariable("subject", subject);
		context.setVariable("content", content);
		return context;
	}
}
