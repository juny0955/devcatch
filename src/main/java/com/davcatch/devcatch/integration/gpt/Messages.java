package com.davcatch.devcatch.integration.gpt;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Messages {
	private String role;
	private String content;

	public static List<Messages> createRequest(String content, String sysPrompt) {
		return List.of(Messages.systemRequest(sysPrompt), Messages.userRequest(content));
	}

	/**
	 * GPT System Request 생성
	 * @return System Request
	 */
	public static Messages systemRequest(String sysPrompt) {
		return Messages.builder()
			.role("system")
			.content(sysPrompt)
			.build();
	}

	/**
	 * GPT User Request 생성
	 * @param content 본문 내용
	 * @return User Request
	 */
	public static Messages userRequest(String content) {
		return Messages.builder()
			.role("user")
			.content(content.substring(0, 200)) // TODO 응답 정확도 보면서 조정해야함
			.build();
	}
}
