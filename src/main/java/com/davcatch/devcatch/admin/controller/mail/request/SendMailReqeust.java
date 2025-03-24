package com.davcatch.devcatch.admin.controller.mail.request;

import lombok.Data;

@Data
public class SendMailReqeust {

	private String subject;
	private String content;
}
