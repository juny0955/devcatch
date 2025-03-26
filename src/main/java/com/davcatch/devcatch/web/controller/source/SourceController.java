package com.davcatch.devcatch.web.controller.source;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.web.controller.source.response.ListResponse;
import com.davcatch.devcatch.web.service.source.SourceService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/source")
public class SourceController {

	private final SourceService sourceService;

	@GetMapping("/list")
	public String sourceList(Model model) {
		List<Source> sources = sourceService.getActiveSources();
		List<ListResponse> sourcesResponse = sources.stream()
				.map(ListResponse::from)
				.toList();

		model.addAttribute("sources", sourcesResponse);
		model.addAttribute("activeMenu", "sources");
		return "web/source/list";
	}
}
