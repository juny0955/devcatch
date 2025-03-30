package com.davcatch.devcatch.web.controller.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.davcatch.devcatch.web.controller.auth.request.RegRequest;
import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;
import com.davcatch.devcatch.web.service.auth.AuthService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {

	private final AuthService authService;

	@GetMapping(value = "/signup")
	public String reg() {
		return "web/auth/signup";
	}

	@PostMapping(value = "/signup")
	public String doReg(@Valid RegRequest request, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		log.info("{} -> 회원가입 요청", request.getEmail());
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			log.info("{} -> 회원가입 요청 중 에러: {}", request.getEmail(), bindingResult.getAllErrors());
			return "redirect:/auth/signup";
		}

		try {
			authService.verify(request);
		} catch (CustomException e) {
			if (e.getErrorCode().equals(ErrorCode.EXISTS_EMAIL))
				redirectAttributes.addFlashAttribute("error", "이미 가입된 이메일입니다.");
			else if (e.getErrorCode().equals(ErrorCode.SERVER_ERROR))
				redirectAttributes.addFlashAttribute("error", "서버 오류로인해 잠시후 다시 시도해주세요.");

			log.info("{} -> 회원 가입 요청 중 에러: {}", request.getEmail(), e.getErrorCode());
			return "redirect:/auth/signup";
		}

		return "redirect:/auth/email/verify";
	}

	@GetMapping("/email/verify")
	public String verify() {
		return "web/auth/inputVerifyCode";
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

		return "web/auth/welcome";
	}

	@PostMapping("/leave")
	public String doLeave(String email) {
		log.info("{} -> 회원탈퇴 요청");
		authService.leave(email);
		return "redirect:/auth/goodbye";
	}

	@GetMapping("/goodbye")
	public String goodbye() {
		return "web/auth/leave";
	}

	@GetMapping("/login")
	public String login(HttpSession session, Model model) {
		String errorMessage = (String) session.getAttribute("error");
		if (errorMessage != null) {
			model.addAttribute("error", errorMessage);
			session.removeAttribute("error");
		}
		return "web/auth/login";
	}

	@GetMapping("/find/password")
	public String findPassword() {
		return "web/auth/findPassword";
	}

	@PostMapping("/find/password")
	public String doFindPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
		log.info("{} -> 비밀번호 찾기 요청");
		try {
			authService.findPassword(email);
		} catch (CustomException e) {
			if (e.getErrorCode() == ErrorCode.MEMBER_NOT_FOUND)
				redirectAttributes.addFlashAttribute("error", "입력하신 이메일로 가입된 계정을 찾을 수 없습니다.");
			else
				redirectAttributes.addFlashAttribute("error", "비밀번호 재설정 요청 중 오류가 발생했습니다.");

			return "redirect:/auth/find/password";
		}

		return "redirect:/auth/login";
	}
}
