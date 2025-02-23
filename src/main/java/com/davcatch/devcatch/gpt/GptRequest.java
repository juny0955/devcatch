package com.davcatch.devcatch.gpt;

import java.util.List;

import lombok.Data;

@Data
public class GptRequest {

	private String model;
	private List<Messages> messages;
}
