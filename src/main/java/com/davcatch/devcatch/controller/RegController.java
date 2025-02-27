package com.davcatch.devcatch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.exception.ErrorCode;
import com.davcatch.devcatch.service.reg.RegService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/devcatch/reg")
public class RegController {

	private final RegService regService;

	@GetMapping(value = {"/", ""})
	public String reg() {
		return "reg/regEmail";
	}

	@PostMapping(value = {"/", ""})
	public String doReg(@Valid RegRequest request, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/devcatch/reg";
		}

		try {
			regService.verify(request);
		} catch (CustomException e) {
			if (e.getErrorCode().equals(ErrorCode.EXISTS_EMAIL))
				redirectAttributes.addFlashAttribute("error", "이미 가입된 이메일입니다.");
			else if (e.getErrorCode().equals(ErrorCode.SERVER_ERROR))
				redirectAttributes.addFlashAttribute("error", "서버 오류로인해 잠시후 다시 시도해주세요.");

			return "redirect:/devcatch/reg";
		}

		return "redirect:/devcatch/reg/verifyCode";
	}

	@GetMapping("/verifyCode")
	public String verify() {
		return "reg/inputVerifyCode";
	}

	@PostMapping("/verify")
	public String doVerify(@RequestParam String verifyCode, RedirectAttributes redirectAttributes) {

		try {
			regService.register(verifyCode);
		} catch (CustomException e) {
			if (e.getErrorCode().equals(ErrorCode.VERIFY_CODE_EXPIRED)) {
				redirectAttributes.addFlashAttribute("error", "인증 시간이 만료되었습니다. 처음부터 다시 시도해주세요");
				return "redirect:/devcatch/reg/";
			} else if (e.getErrorCode().equals(ErrorCode.VERIFY_CODE_WRONG))
				redirectAttributes.addFlashAttribute("error", "잘못된 인증코드입니다");

			return "redirect:/devcatch/reg/verifyCode";
		}

		return "reg/welcome";
	}
}
