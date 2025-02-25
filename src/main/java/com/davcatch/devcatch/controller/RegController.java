package com.davcatch.devcatch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.exception.ErrorCode;
import com.davcatch.devcatch.service.reg.RegService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/devcatch/reg")
public class RegController {

	private final RegService regService;

	@GetMapping("/")
	public String reg() {
		return "reg/regEmail";
	}

	@PostMapping("/")
	public String doReg(RegRequest request, RedirectAttributes redirectAttributes) {
		try {
			regService.verify(request);
		} catch (CustomException e) {
			if (e.getErrorCode().equals(ErrorCode.EXISTS_EMAIL))
				redirectAttributes.addAttribute("error", "중복된 이메일입니다.");
			else if (e.getErrorCode().equals(ErrorCode.SERVER_ERROR))
				redirectAttributes.addAttribute("error", "서버오류로인해 잠시후 다시 시도해주세요.");

			return "redirect:/devcatch/reg";
		}

		return "redirect:/devcatch/reg/verifyCode";
	}

	@GetMapping("/verifyCode")
	public String verify() {
		return "reg/verifyCode";
	}

	@PostMapping("/verify")
	public String doVerify(String email, String verifyCode, RedirectAttributes redirectAttributes) throws CustomException {
		try {
			regService.register(email, verifyCode);
		} catch (CustomException e) {
			if (e.getErrorCode().equals(ErrorCode.VERIFY_CODE_EXPIRED)) {
				redirectAttributes.addAttribute("error", "시간이 만료되었습니다 다시 시도해주세요");
				return "redirect:/";
			} else if (e.getErrorCode().equals(ErrorCode.VERIFY_CODE_WRONG))
				redirectAttributes.addAttribute("error", "잘못된 인증코드입니다");

			return "redirect:/devcatch/reg/verifyCode";
		}

		return "reg/welcome";
	}
}
