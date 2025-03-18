package com.davcatch.devcatch.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.davcatch.devcatch.controller.admin.request.SendMailReqeust;
import com.davcatch.devcatch.service.admin.AdminService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

	private final AdminService adminService;


	@GetMapping("/send-mail")
	public String sendMail() {
		return "admin/sendMail";
	}

	@PostMapping("/send-mail")
	public String doSendMail(SendMailReqeust reqeust) {
		adminService.sendMailToAllMember(reqeust);
		return "redirect:/admin/send-mail";
	}
}
