package com.davcatch.devcatch.config;

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
		executor.initialize();
		return executor;
	}
}
