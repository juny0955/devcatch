package com.davcatch.devcatch.controller.web.auth;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.davcatch.devcatch.controller.web.auth.request.RegRequest;
import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;
import com.davcatch.devcatch.service.auth.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	@GetMapping(value = "/signup")
	public String reg() {
		return "auth/signup";
	}

	@PostMapping(value = "/signup")
	public String doReg(@Valid RegRequest request, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/auth/signup";
		}

		try {
			authService.verify(request);
		} catch (CustomException e) {
			if (e.getErrorCode().equals(ErrorCode.EXISTS_EMAIL))
				redirectAttributes.addFlashAttribute("error", "이미 가입된 이메일입니다.");
			else if (e.getErrorCode().equals(ErrorCode.SERVER_ERROR))
				redirectAttributes.addFlashAttribute("error", "서버 오류로인해 잠시후 다시 시도해주세요.");

			return "redirect:/auth/signup";
		}

		return "redirect:/auth/email/verify";
	}

	@GetMapping("/email/verify")
	public String verify() {
		return "auth/inputVerifyCode";
	}

	@PostMapping("/email/verify")
	public String doVerify(@RequestParam String verifyCode, RedirectAttributes redirectAttributes) {

		try {
			authService.register(verifyCode);
		} catch (CustomException e) {
			if (e.getErrorCode().equals(ErrorCode.VERIFY_CODE_EXPIRED)) {
				redirectAttributes.addFlashAttribute("error", "인증 시간이 만료되었습니다. 처음부터 다시 시도해주세요");
				return "redirect:/auth/signup";
			} else if (e.getErrorCode().equals(ErrorCode.VERIFY_CODE_WRONG))
				redirectAttributes.addFlashAttribute("error", "잘못된 인증코드입니다");

			return "redirect:/auth/email/verify";
		}

		return "auth/welcome";
	}

	@PostMapping("/leave")
	public String doLeave(String email) {
		authService.leave(email);
		return "redirect:/auth/goodbye";
	}

	@GetMapping("/goodbye")
	public String goodbye() {
		return "auth/leave";
	}

	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}
}
