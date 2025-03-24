package com.davcatch.devcatch.controller.admin.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.davcatch.devcatch.service.admin.article.AdminArticleService;
import com.davcatch.devcatch.service.admin.member.AdminMemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/dashboard")
public class DashBoardController {

	private final AdminMemberService memberService;
	private final AdminArticleService articleService;

	@GetMapping(value = {"/", ""})
	public String dashBoard(Model model) {
		model.addAttribute("members", memberService.getDashBoardMemberList());
		model.addAttribute("articles", articleService.getDashBoardArticleList());
		return "admin/dashboard";
	}
}
