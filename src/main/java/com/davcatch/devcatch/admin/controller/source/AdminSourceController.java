package com.davcatch.devcatch.admin.controller.source;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.davcatch.devcatch.admin.controller.source.request.SourceAddRequest;
import com.davcatch.devcatch.admin.service.source.AdminSourceService;
import com.davcatch.devcatch.domain.source.Source;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/sources")
@Slf4j
public class AdminSourceController {

	private final AdminSourceService adminSourceService;

	/**
	 * 소스 목록 페이지
	 */
	@GetMapping(value = {"", "/"})
	public String sourceList(Model model) {
		List<Source> sources = adminSourceService.getSourceList();

		// 활성 및 비활성 소스 개수 계산
		long activeCount = sources.stream().filter(Source::isActive).count();
		long inactiveCount = sources.size() - activeCount;

		model.addAttribute("sources", sources);
		model.addAttribute("activeCount", activeCount);
		model.addAttribute("inactiveCount", inactiveCount);
		model.addAttribute("activeMenu", "sources");
		return "admin/sources";
	}

	/**
	 * 소스 추가 페이지
	 */
	@GetMapping("/add")
	public String sourceAddForm() {
		return "admin/source_add";
	}

	/**
	 * 소스 추가 처리
	 */
	@PostMapping("/add")
	public String sourceAdd(@Valid SourceAddRequest request, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		// 유효성 검증 실패 시 에러 메시지와 함께 폼으로 리다이렉트
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", true);
			redirectAttributes.addFlashAttribute("resultMessage", "입력 값이 올바르지 않습니다. 필수 항목을 모두 입력해주세요.");
			return "redirect:/admin/sources/add";
		}

		try {
			// DTO를 엔티티로 변환하여 저장
			Source source = request.toEntity();
			adminSourceService.addSource(source);

			redirectAttributes.addFlashAttribute("resultMessage", "소스가 성공적으로 추가되었습니다.");
			return "redirect:/admin/sources";
		} catch (Exception e) {
			log.error("소스 추가 중 오류 발생: {}", e.getMessage(), e);
			redirectAttributes.addFlashAttribute("error", true);
			redirectAttributes.addFlashAttribute("resultMessage", "소스 추가 중 오류가 발생했습니다: " + e.getMessage());
			return "redirect:/admin/sources/add";
		}
	}
}