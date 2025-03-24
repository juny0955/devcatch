package com.davcatch.devcatch.integration.gpt.request;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.davcatch.devcatch.integration.gpt.Messages;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GptRequest {

	private String model;
	private List<Messages> messages;

	public static GptRequest create(String content, String model, String sysPrompt) {
		return GptRequest.builder()
			.model(model)
			.messages(Messages.from(content, sysPrompt))
			.build();
	}
}
