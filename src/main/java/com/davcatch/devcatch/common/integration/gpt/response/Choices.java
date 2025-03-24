package com.davcatch.devcatch.common.integration.gpt.response;

import com.davcatch.devcatch.common.integration.gpt.Messages;

import lombok.Data;

@Data
public class Choices {

	private int index;
	private Messages message;
}
