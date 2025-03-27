package com.davcatch.devcatch.web.controller.member;

import java.util.Arrays;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.davcatch.devcatch.web.controller.member.request.ChangePasswordRequest;
import com.davcatch.devcatch.web.controller.member.request.ChangeSubscribeReqeust;
import com.davcatch.devcatch.domain.member.Member;
import com.davcatch.devcatch.domain.tag.TagType;
import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;
import com.davcatch.devcatch.web.service.member.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/mypage")
	public String mypage(@AuthenticationPrincipal Member member, Model model) {
		model.addAttribute("member", member);
		return "web/member/mypage";
	}

	@GetMapping("/setting/subscribe")
	public String settingSubscribe(
		@AuthenticationPrincipal Member member,
		Model model,
		RedirectAttributes redirectAttributes) {

		try {
			model.addAttribute("member", memberService.getMemberWithTag(member.getId()));
			model.addAttribute("availableTags", Arrays.stream(TagType.values()).toList());
		} catch (CustomException e) {
			if (e.getErrorCode() == ErrorCode.MEMBER_NOT_FOUND)
				redirectAttributes.addFlashAttribute("error", "서버에서 회원정보를 찾지못하였습니다 문제가 지속된다면 관리자에게 문의하세요");
			else
				redirectAttributes.addFlashAttribute("error", "서버오류로 인해 다시 시도해주세요");
			return "redirect:/member/mypage";
		}

		return "web/member/setting/subscribe";
	}

	@PostMapping("/setting/subscribe")
	public String doSettingSubscribe(
		@AuthenticationPrincipal Member member,
		ChangeSubscribeReqeust reqeust,
		RedirectAttributes redirectAttributes) {

		log.info("{} -> 구독 설정 변경 요청", member.getEmail());
		try {
			memberService.changeSubscribe(member.getId(), reqeust.isSubscribeAll(), reqeust.getSelectedTags());
			redirectAttributes.addFlashAttribute("message", "정상적으로 변경되었습니다");
		} catch (CustomException e) {
			if (e.getErrorCode() == ErrorCode.BAD_REQUEST)
				redirectAttributes.addFlashAttribute("error", "개별 구독을 선택하신경우 최소 1개 이상의 태그를 선택해주세요");
			else if (e.getErrorCode() == ErrorCode.MEMBER_NOT_FOUND)
				redirectAttributes.addFlashAttribute("error", "서버에서 회원정보를 찾지못하였습니다 문제가 지속된다면 관리자에게 문의하세요");

			return "redirect:/member/setting/subscribe";
		}

		return "redirect:/member/mypage";
	}

	@GetMapping("/setting/password")
	public String settingPassword() {
		return "web/member/setting/password";
	}

	@PostMapping("/setting/password")
	public String doSettingPassword(
		@AuthenticationPrincipal Member member,
		@Valid ChangePasswordRequest request,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes) {

		log.info("{} -> 비밀번호 변경 요청", member.getEmail());
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/member/setting/password";
		}

		try {
			memberService.changePassword(member.getId(), request);
			redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다");
		} catch (CustomException e) {
			if (e.getErrorCode().equals(ErrorCode.PASSWORD_IS_WRONG))
				redirectAttributes.addFlashAttribute("error", "기존 비밀번호가 맞지 않습니다");
			else if (e.getErrorCode().equals(ErrorCode.BAD_REQUEST))
				redirectAttributes.addFlashAttribute("error", "새로운 비밀번호 입력값이 일치하지 않습니다");
			return "redirect:/member/setting/password";
		}

		return "redirect:/member/mypage";
	}
}
