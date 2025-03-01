package com.davcatch.devcatch.service.schedue;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.service.schedue.article.ArticleSchedulerService;
import com.davcatch.devcatch.service.schedue.notification.ArticleNotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerRunner {

	private final List<ArticleSchedulerService> articleSchedulerServices;
	private final ArticleNotificationService articleNotificationService;

	/**
	 * 매일 자정 Article 생성 스케줄러 시작
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	public void runAllArticleScheduler() {
		log.info("전체 Article 생성 스케줄러 시작");
		for (ArticleSchedulerService service : articleSchedulerServices) {
			try {
				service.createNewArticles();
			} catch (CustomException e) {
				log.error("스케줄러 실행 중 에러 발생: {}", e.getErrorCode());
			}
		}
		log.info("전체 Article 생성 스케줄러 종료");
	}

	/**
	 * 매일 오전 8시 새로운 Article 알림 메일 발송 스케줄러 시작
	 */
	@Scheduled(cron = "0 0 8 * * ?")
	public void runSendNewArticlesScheduler() {
		log.info("새로운 Article 전송 스케줄러 시작");

		articleNotificationService.sendNewArticle();

		log.info("새로운 Article 전송 스케줄러 종료");
	}
}
