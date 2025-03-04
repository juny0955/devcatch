package com.davcatch.devcatch.service.schedue.notification;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import com.davcatch.devcatch.domain.Article;
import com.davcatch.devcatch.domain.Member;
import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.repository.ArticleRepository;
import com.davcatch.devcatch.repository.MemberRepository;
import com.davcatch.devcatch.service.mail.MailService;
import com.davcatch.devcatch.service.mail.MailTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ArticleNotificationService {

	private final ArticleRepository articleRepository;
	private final MemberRepository memberRepository;
	private final MailService mailService;

	public void sendNewArticle() {
		List<Article> articles = articleRepository.findSendArticles();
		if (articles.isEmpty()) {
			log.info("보낼 새로운 Article이 없습니다");
			return;
		}
		log.info("새로운 Article 총 {}개 전송 시작", articles.size());

		List<Member> members = memberRepository.findAll();

		Context context = new Context();
		context.setVariable("articles", articles);

		int failCount = 0;
		for (Member member : members) {
			try {
				context.setVariable("email", member.getEmail());
				mailService.sendMail(member.getEmail(), MailTemplate.SEND_ARTICLE, context);
			} catch (CustomException e) {
				log.error("회원 {}에게 메일 전송 실패", member.getEmail());
				failCount++;
			}
		}
		log.info("새로운 Article {}개, 회원 {}명에게 전송 완료, 실패 {}명", articles.size(), (members.size() - failCount), failCount);

		articles.forEach(Article::sendArticle);
		articleRepository.saveAll(articles);
	}
}
