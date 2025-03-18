package com.davcatch.devcatch.controller.admin.request;

import lombok.Data;

@Data
public class SendMailReqeust {

	private String subject;
	private String content;
}
