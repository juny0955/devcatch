package com.davcatch.devcatch.common.config;

import java.time.Duration;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36";
	private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
	private static final Duration READ_TIMEOUT = Duration.ofSeconds(10);

	private HttpComponentsClientHttpRequestFactory createHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setConnectTimeout(CONNECT_TIMEOUT);
		factory.setReadTimeout(READ_TIMEOUT);

		return factory;
	}

	@Bean
	public RestClient rssRestClient() {
		return RestClient.builder()
			.requestFactory(createHttpRequestFactory())
			.defaultHeader("User-Agent", USER_AGENT)
			.defaultHeader("Accept", "application/rss+xml, application/xml, text/xml, */*")
			.build();
	}

	@Bean
	public RestClient cloudflareRssRestClient() {
		HttpClient httpClient = HttpClients.custom()
			.setDefaultCookieStore(new BasicCookieStore())
			.setUserAgent(USER_AGENT)
			.build();

		HttpComponentsClientHttpRequestFactory factory = createHttpRequestFactory();
		factory.setHttpClient(httpClient);

		return RestClient.builder()
			.requestFactory(factory)
			.defaultHeader("User-Agent", USER_AGENT)
			.defaultHeader("Accept", "application/rss+xml, application/xml, text/xml, */*")
			.build();
	}

	@Bean
	public RestClient crawlingRestClient() {
		return RestClient.builder()
			.requestFactory(createHttpRequestFactory())
			.defaultHeader("User-Agent", USER_AGENT)
			.defaultHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.build();
	}

	@Bean
	public RestClient gptApiRestClient(@Value("${gpt.key}") String apiKey) {
		return RestClient.builder()
			.requestFactory(createHttpRequestFactory())
			.defaultHeader("Authorization", "Bearer " + apiKey)
			.defaultHeader("Content-Type", "application/json")
			.build();
	}
}
