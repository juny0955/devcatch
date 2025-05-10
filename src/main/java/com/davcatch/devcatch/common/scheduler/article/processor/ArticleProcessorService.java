package com.davcatch.devcatch.common.scheduler.article.processor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.stereotype.Service;

import com.davcatch.devcatch.common.scheduler.article.dto.ArticleSummary;
import com.davcatch.devcatch.common.scheduler.article.dto.ParsedArticle;
import com.davcatch.devcatch.common.scheduler.article.processor.summary.ArticleSummaryService;
import com.davcatch.devcatch.common.service.cache.LastArticleCacheService;
import com.davcatch.devcatch.common.util.ArticleUtil;
import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.domain.tag.Tag;
import com.davcatch.devcatch.domain.tag.TagType;
import com.davcatch.devcatch.web.service.article.ArticleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleProcessorService {

	private final ArticleSummaryService articleSummaryService;
	private final ArticleService articleService;
	private final LastArticleCacheService lastArticleCacheService;
	private final Executor gptSummaryTaskExecutor;

	public List<Article> processParsedArticles(Source source, Map<TagType, Tag> tagMap, List<ParsedArticle> parsedArticles) {
		log.debug("[{}] {}개 아티클 처리 시작", source.getName(), parsedArticles.size());

		List<CompletableFuture<Article>> futures = parsedArticles.stream()
			.map(parsedArticle -> CompletableFuture.supplyAsync(() -> {
				try {
					ArticleSummary summary = articleSummaryService.summarizeArticle(parsedArticle.getContent());

					List<Tag> tags = summary.getTags().stream()
						.map(tagMap::get)
						.filter(Objects::nonNull)
						.toList();

					Article article = ArticleUtil.createNewArticle(source, parsedArticle, summary, tags);

					articleService.save(article);
					lastArticleCacheService.updateLastPublishedDate(source.getId(), article.getPublishedAt());
					return article;
				} catch (Exception e) {
					log.error("[{}] 아티클 처리 중 오류 발생: {}", source.getName(), e.getMessage(), e);
					return null;
				}
			}, gptSummaryTaskExecutor))
			.toList();

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

		return futures.stream()
			.map(future -> {
				try {
					return future.getNow(null);
				} catch (Exception e) {
					return null;
				}
			})
			.filter(Objects::nonNull)
			.toList();
	}
}
