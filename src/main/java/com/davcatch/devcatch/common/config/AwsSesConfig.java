package com.davcatch.devcatch.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;

/**
 * TODO AWS SDK v2.0 마이그레이션 필요
 * 현재 정보가 부족하여 이전 버전으로 진행
 */
@Configuration
public class AwsSesConfig {

	@Value("${aws.ses.access-key}")
	private String accessKey;

	@Value("${aws.ses.secret-key}")
	private String secretKey;

	@Value("${aws.ses.region}")
	private String region;

	@Bean
	public AmazonSimpleEmailService amazonSimpleEmailService() {
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

		return AmazonSimpleEmailServiceClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withRegion(Regions.fromName(region))
			.build();
	}
}
