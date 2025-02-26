package com.davcatch.devcatch.service.mail;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.exception.ErrorCode;

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
	 * @throws CustomException
	 */
	public void sendMail(String email, MailTemplate template, Context context) throws CustomException {
		try {
			log.info("메일 발송 시작");
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

			messageHelper.setSubject(template.getTitle());
			messageHelper.setTo(email);

			String htmlContent = templateEngine.process(template.getTemplatePath(), context);
			messageHelper.setText(htmlContent, true);

			javaMailSender.send(message);
			log.info("메일 발송 정상 종료 : {}", email);
		} catch (Exception e) {
			log.error("메일 발송 중 오류 발생 : {}", e.getMessage());
			throw new CustomException(ErrorCode.SERVER_ERROR);
		}
	}
}
