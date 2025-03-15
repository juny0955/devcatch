package com.davcatch.devcatch.controller.member;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.davcatch.devcatch.domain.Member;
import com.davcatch.devcatch.domain.TagType;
import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.exception.ErrorCode;
import com.davcatch.devcatch.service.member.MemberService;
import com.davcatch.devcatch.service.tag.TagService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final TagService tagService;

	@GetMapping("/setting/subscribe")
	public String settingSubscribe(Model model, @AuthenticationPrincipal Member member) throws CustomException {
		// try {
			model.addAttribute("member", memberService.getMemberWithTag(member.getId()));
			model.addAttribute("availableTags", Arrays.stream(TagType.values()).toList());
			return "member/setting/subscribe";
		// } catch (CustomException e) {
		//
		// }
	}

	@PostMapping("/setting/subscribe")
	public String doSettingSubscribe(
		@AuthenticationPrincipal Member member,
		ChangeSubscribeReqeust reqeust,
		RedirectAttributes redirectAttributes) {

		try {
			memberService.changeSubscribe(member.getId(), reqeust.isSubscribeAll(), reqeust.getSelectedTags());
			redirectAttributes.addFlashAttribute("message", "정상적으로 변경되었습니다");
		} catch (CustomException e) {
			if (e.getErrorCode() == ErrorCode.BAD_REQUEST)
				redirectAttributes.addFlashAttribute("error", "개별 구독을 선택하신경우 최소 1개 이상의 태그를 선택해주세요");
			else if (e.getErrorCode() == ErrorCode.MEMBER_NOT_FOUND)
				redirectAttributes.addFlashAttribute("error", "서버에서 회원정보를 찾지못하였습니다 문제가 지속된다면 관리자에게 문의하세요");
		}

		return "redirect:/member/setting/subscribe";
	}
}
