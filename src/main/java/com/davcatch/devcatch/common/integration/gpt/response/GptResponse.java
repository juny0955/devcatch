package com.davcatch.devcatch.common.integration.gpt.response;

import java.util.List;

import lombok.Data;

@Data
public class GptResponse {

	private List<Choices> choices;
}
