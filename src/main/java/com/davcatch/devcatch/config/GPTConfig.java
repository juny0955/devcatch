package com.davcatch.devcatch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GPTConfig {

	@Value("${gpt.key}")
	private String apiKey;

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add(((request, body, execution) -> {
			request.getHeaders().add("Authorization", "bearer " + apiKey);
			return execution.execute(request, body);
		}));

		return restTemplate;
	}
}
