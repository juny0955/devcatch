package com.davcatch.devcatch.service.common.mail;

import java.util.concurrent.CompletableFuture;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

	private final JavaMailSender javaMailSender;
	private final TemplateEngine templateEngine;

	/**
	 * 메일 보내기
	 * @param email 보낼 이메일
	 * @param template 메일 정보
	 * @param context thymeleaf attribute
	 */
	@Async("mailTaskExecutor")
	public CompletableFuture<Void> sendMail(String email, MailTemplate template, Context context) {
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

			messageHelper.setSubject(template.getTitle());
			messageHelper.setTo(email);

			String htmlContent = templateEngine.process(template.getTemplatePath(), context);
			messageHelper.setText(htmlContent, true);

			javaMailSender.send(message);
			return CompletableFuture.completedFuture(null);
		} catch (Exception e) {
			log.error("[{}] 메일 발송중 에러 발생: {}", email, e.getMessage());
			return CompletableFuture.failedFuture(new CustomException(ErrorCode.MAIL_SEND_FAIL));
		}
	}
}
