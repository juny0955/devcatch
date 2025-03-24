package com.davcatch.devcatch.web.controller.article;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.davcatch.devcatch.web.controller.article.response.ArticleResponse;
import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.article.ArticleTag;
import com.davcatch.devcatch.domain.tag.TagType;
import com.davcatch.devcatch.web.service.article.ArticleService;
import com.davcatch.devcatch.web.service.article.ArticleTagService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {

	private final ArticleService articleService;
	private final ArticleTagService articleTagService;

	@GetMapping("/list")
	public String list(
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) TagType tag,
		@PageableDefault(size = 10, sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable,
		Model model) {

		Page<Article> articles = articleService.getArticlesList(pageable);
		List<ArticleTag> articleTags = articleTagService.getArticleTagByArticlesWithTag(articles.stream().toList());

		List<ArticleResponse> articleResponses = articles.stream()
			.map(article -> ArticleResponse.of(article, articleTags))
			.toList();

		Page<ArticleResponse> articleResponsePage = new PageImpl<>(
			articleResponses,
			pageable,
			articles.getTotalElements()
		);

		model.addAttribute("articles", articleResponsePage);
		model.addAttribute("availableTags", Arrays.asList(TagType.values()));
		model.addAttribute("activeMenu", "articles");
		return "web/article/list";
	}
}
