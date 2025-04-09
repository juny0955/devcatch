package com.davcatch.devcatch.common.integration.gpt.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GptResponse {

	private List<Choices> choices;
}
