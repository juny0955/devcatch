package com.davcatch.devcatch.common.scheduler.article.processor;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.stereotype.Service;

import com.davcatch.devcatch.common.scheduler.article.dto.ArticleSummary;
import com.davcatch.devcatch.common.scheduler.article.dto.ParsedArticle;
import com.davcatch.devcatch.common.scheduler.article.processor.summary.ArticleSummaryService;
import com.davcatch.devcatch.common.scheduler.article.util.ArticleUtil;
import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.domain.tag.Tag;
import com.davcatch.devcatch.web.service.article.ArticleService;
import com.davcatch.devcatch.web.service.tag.TagService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleProcessorService {

	private final ArticleSummaryService articleSummaryService;
	private final TagService tagService;
	private final ArticleService articleService;
	private final Executor gptSummaryTaskExecutor;

	public List<Article> processParsedArticles(Source source, List<ParsedArticle> parsedArticles) {
		log.debug("[{}] {}개 아티클 처리 시작", source.getName(), parsedArticles.size());

		List<CompletableFuture<Article>> futures = parsedArticles.stream()
			.map(parsedArticle -> CompletableFuture.supplyAsync(() -> {
				try {
					ArticleSummary summary = articleSummaryService.summarizeArticle(parsedArticle.getContent());
					List<Tag> tags = tagService.getInTagTypes(summary.getTags());
					Article article = ArticleUtil.createNewArticle(source, parsedArticle, summary, tags);

					articleService.save(article);
					return article;
				} catch (Exception e) {
					log.error("[{}] 아티클 처리 중 오류 발생: {}", source.getName(), e.getMessage(), e);
					return null;
				}
			}, gptSummaryTaskExecutor))
			.toList();

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

		List<Article> processedArticles = futures.stream()
			.map(future -> {
				try {
					return future.get();
				} catch (Exception e) {
					log.error("[{}] CompletableFuter 결과 가져오기 실패: {}", source.getName(), e.getMessage(), e);
					return null;
				}
			})
			.filter(Objects::nonNull)
			.toList();

		log.debug("[{}] {}개 아티클 처리 완료", source.getName(), processedArticles.size());
		return processedArticles;
	}
}
