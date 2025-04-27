package com.davcatch.devcatch.common.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

	/**
	 * 메일서비스 스레드풀 설정
	 * 현재 AWS EC2 t2.micro 사용중
	 * @return executor
	 */
	@Bean(name = "mailTaskExecutor")
	public Executor mailExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(4);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("MailExecutor-");
		executor.setRejectedExecutionHandler((r, e) -> {
			Thread.currentThread().getThreadGroup().uncaughtException(
				Thread.currentThread(), new RuntimeException("Mail 발송 스레드풀이 포화상태입니다")
			);
		});
		executor.initialize();
		return executor;
	}

	/**
	 * 아티클 스케줄러 스레드풀 설정
	 * 현재 AWS EC2 t2.micro 사용중
	 * @return executor
	 */
	@Bean(name = "schedulerTaskExecutor")
	public Executor schedulerExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(3);
		executor.setMaxPoolSize(5);
		executor.setQueueCapacity(15);
		executor.setThreadNamePrefix("ArticleScheduler-");
		executor.setRejectedExecutionHandler((r, e) -> {
			// 큐가 가득찼을 때 처리 (로그 기록)
			Thread.currentThread().getThreadGroup().uncaughtException(
				Thread.currentThread(),
				new RuntimeException("ArticleScheduler 스레드풀이 포화상태입니다.")
			);
		});
		executor.initialize();
		return executor;
	}

	/**
	 * gpt 요약 스레드풀 설정
	 * @return executor
	 */
	@Bean(name = "gptSummaryTaskExecutor")
	public Executor gptSummaryTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(1);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(10);
		executor.setThreadNamePrefix("GptSummary-");
		executor.setRejectedExecutionHandler((r, e) -> {
			Thread.currentThread().getThreadGroup().uncaughtException(
				Thread.currentThread(), new RuntimeException("GPT 요약 스레드풀이 포화상태입니다")
			);
		});
		executor.initialize();
		return executor;
	}
}
