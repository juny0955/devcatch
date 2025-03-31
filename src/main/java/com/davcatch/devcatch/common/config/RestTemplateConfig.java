package com.davcatch.devcatch.common.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

	@Bean
	public RestTemplate rssRestTemplate(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder
			.connectTimeout(Duration.ofSeconds(10))
			.readTimeout(Duration.ofSeconds(10))
			.defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
			.defaultHeader("Accept", "application/rss+xml, application/xml, text/xml, */*")
			.build();
	}

	@Bean
	public RestTemplate crawlingRestTemplate(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder
			.connectTimeout(Duration.ofSeconds(10))
			.readTimeout(Duration.ofSeconds(10))
			.defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
			.defaultHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.build();
	}

	@Bean
	public RestTemplate gptApiRestTemplate(RestTemplateBuilder restTemplateBuilder, @Value("${gpt.key}") String apiKey) {
		return restTemplateBuilder
			.connectTimeout(Duration.ofSeconds(30))
			.readTimeout(Duration.ofSeconds(30))
			.defaultHeader("Authorization", "Bearer "+apiKey)
			.defaultHeader("Content-Type", "application/json")
			.build();
	}
}
