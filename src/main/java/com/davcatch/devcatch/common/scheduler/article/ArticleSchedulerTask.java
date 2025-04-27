package com.davcatch.devcatch.common.scheduler.article;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.davcatch.devcatch.common.scheduler.article.dto.ParsedArticle;
import com.davcatch.devcatch.common.scheduler.article.parser.ArticleParseService;
import com.davcatch.devcatch.common.scheduler.article.processor.ArticleProcessorService;
import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.domain.tag.Tag;
import com.davcatch.devcatch.domain.tag.TagType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleSchedulerTask {

	private final ArticleParseService articleParseService;
	private final ArticleProcessorService articleProcessorService;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void processSource(Source source, Map<TagType, Tag> tagMap) {
		try {
			List<ParsedArticle> collectedArticles = articleParseService.parseArticles(source);

			if (collectedArticles.isEmpty()) {
				log.info("[{}] 새로운 아티클이 없습니다", source.getName());
				return;
			}

			log.info("[{}] {}개 아티클 수집 완료", source.getName(), collectedArticles.size());

			List<Article> processedArticles = articleProcessorService.processParsedArticles(source, tagMap, collectedArticles);

			log.info("[{}] {}개 아티클 처리 완료", source.getName(), processedArticles.size());
		} catch (Exception e) {
			log.error("[{}] 소스 처리 중 오류 발생: {}", source.getName(), e.getMessage(), e);
		}
	}
}
