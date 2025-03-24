package com.davcatch.devcatch.integration.gpt.response;

import com.davcatch.devcatch.integration.gpt.Messages;

import lombok.Data;

@Data
public class Choices {

	private int index;
	private Messages message;
}
