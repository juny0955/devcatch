package com.davcatch.devcatch.schedue.article.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.schedue.article.dto.ParsedArticle;
import com.davcatch.devcatch.service.article.ArticleService;
import com.davcatch.devcatch.service.tag.TagService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleProcessorService {

	private final ArticleSummaryService articleSummaryService;
	private final ArticleTaggerService articleTaggerService;
	private final TagService tagService;
	private final ArticleService articleService;

	public List<Article> processParsedArticles(Source source, List<ParsedArticle> parsedArticles) {
		log.debug("[{}] {}개 아티클 처리 시작", source.getName(), parsedArticles.size());

		List<Article> processedArticles = new ArrayList<>();

		for (ParsedArticle parsedArticle : parsedArticles) {
			try {
				Article article = processArticle(source, parsedArticle);
				if (article != null)
					processedArticles.add(article);
			} catch (Exception e) {
				log.error("[{}] 아티클 처리 중 오류 발생: {}", source.getName(), e.getMessage(), e);
			}
		}

		log.debug("[{}] {}개 아티클 처리 완료", source.getName(), processedArticles.size());
		return processedArticles;
	}

	private Article processArticle(Source source, ParsedArticle parsedArticle) {
		ArticleSummary summary = .s
	}
}
