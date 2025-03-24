package com.davcatch.devcatch.common.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.davcatch.devcatch.common.scheduler.article.ArticleSchedulerService;
import com.davcatch.devcatch.common.scheduler.notification.ArticleNotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerRunner {

	private final ArticleSchedulerService articleSchedulerServices;
	private final ArticleNotificationService articleNotificationService;

	/**
	 * 매일 자정 Article 생성 스케줄러 시작
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	public void runCreateNewArticlesScheduler() {
		log.info("==========새로운 Article 생성 스케줄러 시작==========");

		try {
			articleSchedulerServices.createNewArticle();
		} catch (Exception e) {
			log.error("스케줄러 실행 중 에러 발생: {}", e.getMessage());
		}

		log.info("==========새로운 Article 생성 스케줄러 종료==========");
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
