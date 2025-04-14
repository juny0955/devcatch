package com.davcatch.devcatch.common.service.mail;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

	private final AmazonSimpleEmailService amazonSimpleEmailService;
	private final TemplateEngine templateEngine;

	@Value("${aws.ses.sender-email}")
	private String senderEmail;

	/**
	 * 메일 보내기
	 * @param email 보낼 이메일
	 * @param template 메일 정보
	 * @param context thymeleaf attribute
	 */
	@Async("mailTaskExecutor")
	public CompletableFuture<Void> sendMail(String email, MailTemplate template, Context context) {
		try {
			String htmlContent = templateEngine.process(template.getTemplatePath(), context);

			Content subject = new Content()
				.withCharset(StandardCharsets.UTF_8.name())
				.withData(template.getTitle());

			Body body = new Body()
				.withHtml(new Content()
					.withCharset(StandardCharsets.UTF_8.name())
					.withData(htmlContent));

			Message message = new Message()
				.withSubject(subject)
				.withBody(body);

			SendEmailRequest request = new SendEmailRequest()
				.withSource(senderEmail)
				.withDestination(new Destination().withToAddresses(email))
				.withMessage(message);

			amazonSimpleEmailService.sendEmail(request);

			return CompletableFuture.completedFuture(null);
		} catch (Exception e) {
			log.error("[{}] 메일 발송중 에러 발생: {}", email, e.getMessage());
			return CompletableFuture.failedFuture(new CustomException(ErrorCode.MAIL_SEND_FAIL));
		}
	}
}
