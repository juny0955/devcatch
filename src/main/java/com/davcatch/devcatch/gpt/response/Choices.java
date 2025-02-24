package com.davcatch.devcatch.gpt.response;

import java.util.List;

import com.davcatch.devcatch.gpt.Messages;

import lombok.Data;

@Data
public class Choices {

	private int index;
	private Messages message;
}
