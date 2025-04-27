package com.davcatch.devcatch.common.scheduler.notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.service.mail.MailService;
import com.davcatch.devcatch.common.service.mail.MailTemplate;
import com.davcatch.devcatch.common.util.MailUtil;
import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.member.Member;
import com.davcatch.devcatch.domain.tag.TagType;
import com.davcatch.devcatch.web.service.article.ArticleService;
import com.davcatch.devcatch.web.service.member.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleNotificationService {

	private final ArticleService articleService;
	private final MemberService memberService;
	private final MailService mailService;

	public void sendNewArticle() {
		List<Article> articles = articleService.getSendArticles();
		if (articles.isEmpty()) {
			log.info("보낼 새로운 Article이 없습니다");
			return;
		}
		log.info("새로운 Article 총 {}개 전송 시작", articles.size());

		List<Member> members = memberService.getAllMemberWithTag();
		log.info("총 {}명의 회원에게 알림 전송 시작", members.size());

		List<CompletableFuture<Void>> futures = new ArrayList<>();
		for (Member member : members) {
			List<Article> toSendArticles = filterArticlesForMember(member, articles);

			if (toSendArticles.isEmpty()) {
				log.debug("회원 {}({})에게 전송할 아티클이 없습니다", member.getName(), member.getEmail());
				continue;
			}

			log.debug("회원 {}({})에게 {}개 아티클 전송", member.getName(), member.getEmail(), toSendArticles.size());

			Context context = MailUtil.createNewArticleContext(member, toSendArticles);

			CompletableFuture<Void> future = mailService.sendMail(member.getEmail(), MailTemplate.SEND_ARTICLE, context);
			futures.add(future);
		}

		if (!futures.isEmpty())
			processSendMailResults(futures, articles, members.size());
		else
			log.info("전송할 메일이 없습니다");

		updateArticlesStatus(articles);
	}

	private void processSendMailResults(List<CompletableFuture<Void>> futures, List<Article> articles, int memberSize) {
		CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

		try {
			allFutures.join();
			log.info("전체 메일 전송 완료");
		} catch (Exception e) {
			log.error("메일 전송 중 오류 발생: {}", e.getMessage(), e);
		}

		long successCount = futures.stream()
			.filter(future -> !future.isCompletedExceptionally())
			.count();

		log.info("새로운 Article {}개, 회원 {}명 중 {}명에게 전송 완료, 실패 {}명", articles.size(), memberSize, successCount, futures.size() - successCount);
	}

	private List<Article> filterArticlesForMember(Member member, List<Article> articles) {
		if (member.isSubscribeAll()) {
			return articles.stream()
				.filter(article -> !article.getSource().isForeign() || member.isSubscribeForeign())
				.toList();
		}

		Set<TagType> subscribeTag = member.getMemberTags().stream()
			.map(memberTag -> memberTag.getTag().getTagType())
			.collect(Collectors.toSet());

		return articles.stream()
			.filter(article -> {
				if (article.getSource().isForeign() && !member.isSubscribeForeign())
					return false;

				Set<TagType> articleTags = article.getArticleTags().stream()
					.map(articleTag -> articleTag.getTag().getTagType())
					.collect(Collectors.toSet());

				return !Collections.disjoint(articleTags, subscribeTag);
			})
			.toList();
	}

	private void updateArticlesStatus(List<Article> articles) {
		articles.forEach(article -> {
			article.sendArticle();
			try {
				articleService.save(article);
			} catch (CustomException e) {
				throw new RuntimeException(e);
			}
		});
		log.info("총 {}개 아티클 상태 업데이트 완료", articles.size());
	}
}
