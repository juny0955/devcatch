package com.davcatch.devcatch.common.integration.gpt;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Messages {

	private String role;
	private String content;

	public static List<Messages> from(String content, String sysPrompt) {
		return List.of(systemRequestFrom(sysPrompt), userRequestFrom(content));
	}

	/**
	 * GPT System Request 생성
	 * @return System Request
	 */
	private static Messages systemRequestFrom(String sysPrompt) {
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
	private static Messages userRequestFrom(String content) {
		return Messages.builder()
			.role("user")
			.content(content)
			.build();
	}
}
