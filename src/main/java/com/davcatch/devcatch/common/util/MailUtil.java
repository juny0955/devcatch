package com.davcatch.devcatch.common.util;

import java.util.List;

import org.thymeleaf.context.Context;

import com.davcatch.devcatch.common.service.mail.MailTemplate;
import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.member.Member;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MailUtil {

	public static Context createNewArticleContext(Member member, List<Article> toSendArticles) {
		Context context = new Context();
		context.setVariable("subject", MailTemplate.SEND_ARTICLE.getTitle());
		context.setVariable("email", member.getEmail());
		context.setVariable("articles", toSendArticles);
		return context;
	}

	public static Context createAuthContext(String verifyCode) {
		Context context = new Context();
		context.setVariable("subject", MailTemplate.VERIFY_EMAIL.getTitle());
		context.setVariable("verifyCode", verifyCode);
		return context;
	}

	public static Context createFindPasswordContext(String name, String tempPassword) {
		Context context = new Context();
		context.setVariable("subject", MailTemplate.FIND_PASSWORD.getTitle());
		context.setVariable("name", name);
		context.setVariable("tempPassword", tempPassword);
		return context;
	}

	public static Context createAdminContext(String subject, String content) {
		Context context = new Context();
		context.setVariable("subject", subject);
		context.setVariable("content", content);
		return context;
	}
}
