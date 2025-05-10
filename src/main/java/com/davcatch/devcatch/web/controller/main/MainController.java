package com.davcatch.devcatch.web.controller.main;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.davcatch.devcatch.web.controller.article.response.ArticleResponse;
import com.davcatch.devcatch.web.service.article.ArticleCommendService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

	private final ArticleCommendService articleCommendService;

	@GetMapping(value = {"/", ""})
	public String index(Model model) {
		List<ArticleResponse> articleResponses = articleCommendService.getNewArticles();

		model.addAttribute("articles", articleResponses);
		model.addAttribute("activeMenu", "home");
		return "web/index";
	}
}
