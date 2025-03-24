package com.davcatch.devcatch.service.admin.schduler;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.davcatch.devcatch.scheduler.article.ArticleSchedulerService;
import com.davcatch.devcatch.scheduler.notification.ArticleNotificationService;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

	private final ArticleSchedulerService articleSchedulerService;
	private final ArticleNotificationService articleNotificationService;

	/**
	 * 아티클 생성 스케줄러 수동 실행
	 * @return 실행 결과 메시지
	 */
	public String runArticleCreationScheduler() {
		log.info("아티클 생성 스케줄러 수동 실행");
		LocalDateTime startTime = LocalDateTime.now();

		try {
			articleSchedulerService.createNewArticle();

			// 성공 이력 저장
			SchedulerExecution execution = new SchedulerExecution();
			execution.setSchedulerName("아티클 수집 및 생성");
			execution.setExecutionTime(startTime);
			execution.setStatus("성공");
			execution.setDetails("수동 실행으로 신규 아티클 수집 완료");

			return "아티클 수집 및 생성 스케줄러가 성공적으로 실행되었습니다.";
		} catch (Exception e) {
			log.error("아티클 생성 스케줄러 실행 중 오류: {}", e.getMessage(), e);

			// 실패 이력 저장
			SchedulerExecution execution = new SchedulerExecution();
			execution.setSchedulerName("아티클 수집 및 생성");
			execution.setExecutionTime(startTime);
			execution.setStatus("실패");
			execution.setDetails("오류: " + e.getMessage());

			return "아티클 수집 중 오류가 발생했습니다: " + e.getMessage();
		}
	}

	/**
	 * 아티클 발송 스케줄러 수동 실행
	 * @return 실행 결과 메시지
	 */
	public String runArticleNotificationScheduler() {
		log.info("아티클 발송 스케줄러 수동 실행");
		LocalDateTime startTime = LocalDateTime.now();

		try {
			articleNotificationService.sendNewArticle();

			// 성공 이력 저장
			SchedulerExecution execution = new SchedulerExecution();
			execution.setSchedulerName("아티클 이메일 발송");
			execution.setExecutionTime(startTime);
			execution.setStatus("성공");
			execution.setDetails("수동 실행으로 이메일 발송 완료");

			return "아티클 이메일 발송 스케줄러가 성공적으로 실행되었습니다.";
		} catch (Exception e) {
			log.error("아티클 발송 스케줄러 실행 중 오류: {}", e.getMessage(), e);

			// 실패 이력 저장
			SchedulerExecution execution = new SchedulerExecution();
			execution.setSchedulerName("아티클 이메일 발송");
			execution.setExecutionTime(startTime);
			execution.setStatus("실패");
			execution.setDetails("오류: " + e.getMessage());

			return "아티클 이메일 발송 중 오류가 발생했습니다: " + e.getMessage();
		}
	}

	/**
	 * 스케줄러 실행 이력 DTO
	 */
	@Data
	public static class SchedulerExecution {
		private String schedulerName;
		private LocalDateTime executionTime;
		private String status;
		private String details;
	}
}