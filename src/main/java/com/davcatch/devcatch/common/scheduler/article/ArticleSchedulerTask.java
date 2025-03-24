package com.davcatch.devcatch.common.scheduler.article;

import java.util.List;

import org.springframework.stereotype.Component;

import com.davcatch.devcatch.common.scheduler.article.dto.ParsedArticle;
import com.davcatch.devcatch.common.scheduler.article.parser.ArticleParseService;
import com.davcatch.devcatch.common.scheduler.article.processor.ArticleProcessorService;
import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.source.Source;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleSchedulerTask {

	private final ArticleParseService articleParseService;
	private final ArticleProcessorService articleProcessorService;

	public void processSource(Source source) {
		try {
			List<ParsedArticle> collectedArticles = articleParseService.parseArticles(source);

			if (collectedArticles.isEmpty()) {
				log.info("[{}] 새로운 아티클이 없습니다", source.getName());
				return;
			}

			log.info("[{}] {}개 아티클 수집 완료", source.getName(), collectedArticles.size());

			List<Article> processedArticles = articleProcessorService.processParsedArticles(source, collectedArticles);

			log.info("[{}] {}개 아티클 처리 완료", source.getName(), processedArticles.size());
		} catch (Exception e) {
			log.error("[{}] 소스 처리 중 오류 발생: {}", source.getName(), e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
}
