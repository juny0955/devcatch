package com.davcatch.devcatch.controller.web.source;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.davcatch.devcatch.service.admin.source.SourceService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/source")
public class SourceController {

	private final SourceService sourceService;

	@GetMapping("/list")
	public String list() {
		return "";
	}
}
