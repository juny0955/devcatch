package com.davcatch.devcatch.web.controller.main;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.article.ArticleTag;
import com.davcatch.devcatch.web.controller.article.response.ArticleResponse;
import com.davcatch.devcatch.web.service.article.ArticleService;
import com.davcatch.devcatch.web.service.article.ArticleTagService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

	private final ArticleService articleService;
	private final ArticleTagService articleTagService;

	@GetMapping(value = {"/", ""})
	public String index(Model model) {
		List<Article> articles = articleService.getNewArticles();
		List<ArticleTag> articleTags = articleTagService.getArticleTagByArticlesWithTag(articles);

		List<ArticleResponse> articleResponses = articles.stream()
			.map(article -> ArticleResponse.of(article, articleTags))
			.toList();

		model.addAttribute("articles", articleResponses);
		model.addAttribute("activeMenu", "home");
		return "web/index";
	}
}
