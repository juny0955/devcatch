package com.davcatch.devcatch.controller.member;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.davcatch.devcatch.domain.Member;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

	@GetMapping("/setting/subscribe")
	public String settingSubscribe(Model model, @AuthenticationPrincipal Member member) {
		model.addAttribute("member", member);
		return "member/subscribe";
	}
}
