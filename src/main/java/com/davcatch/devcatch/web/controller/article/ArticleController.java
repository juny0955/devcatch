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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.web.controller.article.response.ArticleResponse;
import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.article.ArticleTag;
import com.davcatch.devcatch.domain.tag.TagType;
import com.davcatch.devcatch.web.service.article.ArticleService;
import com.davcatch.devcatch.web.service.article.ArticleTagService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/article")
@Slf4j
public class ArticleController {

	private final ArticleService articleService;
	private final ArticleTagService articleTagService;

	@GetMapping("/list")
	public String list(
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) TagType tag,
		@PageableDefault(size = 10, sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable,
		Model model) {

		Page<Article> articles = articleService.getArticlesList(pageable, keyword, tag);
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
		model.addAttribute("selectedTag", tag);
		model.addAttribute("activeMenu", "articles");
		return "web/article/list";
	}

	@GetMapping("/detail/{articleId}")
	public String detail(@PathVariable Long articleId, Model model) {
		try {
			Article article = articleService.getArticle(articleId);
			List<ArticleTag> articleTags = articleTagService.getArticleTagByArticlesWithTag(List.of(article));

			List<TagType> tagTypes = articleTags.stream()
				.map(articleTag -> articleTag.getTag().getTagType())
				.toList();

			List<Article> relatedArticles = articleService.getRelatedArticles(articleId, tagTypes);
			List<ArticleTag> relatedArticleTags = articleTagService.getArticleTagByArticlesWithTag(relatedArticles);

			ArticleResponse articleResponse = ArticleResponse.of(article, articleTags);

			List<ArticleResponse> relatedArticleResponses = relatedArticles.stream()
				.map(relatedArticle -> ArticleResponse.of(relatedArticle, relatedArticleTags))
				.toList();

			model.addAttribute("article", articleResponse);
			model.addAttribute("relatedArticles", relatedArticleResponses);
			model.addAttribute("activeMenu", "articles");
		} catch (CustomException e) {
			log.error("아티클 상세 페이지 에러: {}", e.getMessage());
			return "redirect:/article/list";
		}

		return "web/article/detail";
	}
}
