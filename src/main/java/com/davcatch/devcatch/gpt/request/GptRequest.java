package com.davcatch.devcatch.gpt.request;

import java.util.List;

import com.davcatch.devcatch.gpt.Messages;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GptRequest {

	private String model;
	private List<Messages> messages;

	public static GptRequest create(String content) {
		return GptRequest.builder()
			.model("gpt-4o-mini")
			.messages(Messages.createRequest(content))
			.build();
	}
}
