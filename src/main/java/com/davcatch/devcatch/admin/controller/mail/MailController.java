package com.davcatch.devcatch.admin.controller.mail;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.davcatch.devcatch.admin.controller.mail.request.SendMailReqeust;
import com.davcatch.devcatch.admin.service.mail.AdminMailService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class MailController {

	private final AdminMailService adminMailService;

	@GetMapping("/send-mail")
	public String sendMail() {
		return "admin/sendMail";
	}

	@PostMapping("/send-mail")
	public String doSendMail(SendMailReqeust reqeust) {
		adminMailService.sendMailToAllMember(reqeust);
		return "redirect:/admin/send-mail";
	}
}
