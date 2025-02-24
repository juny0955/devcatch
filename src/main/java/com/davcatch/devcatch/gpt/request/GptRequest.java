package com.davcatch.devcatch.gpt.request;

import java.util.List;

import com.davcatch.devcatch.gpt.Messages;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
@Builder
public class GptRequest {

	private String model;
	private List<Messages> messages;

	public static GptRequest create(String model, String content, String sysPrompt) {
		return GptRequest.builder()
			.model(model)
			.messages(Messages.createRequest(content, sysPrompt))
			.build();
	}
}
