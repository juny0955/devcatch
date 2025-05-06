package com.davcatch.devcatch.common.scheduler.article.parser;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.scheduler.article.dto.ParsedArticle;
import com.davcatch.devcatch.common.scheduler.article.parser.factory.ArticleParseStrategyFactory;
import com.davcatch.devcatch.common.scheduler.article.parser.strategy.ArticleParseStrategy;
import com.davcatch.devcatch.common.service.cache.LastArticleCacheService;
import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.web.service.article.ArticleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleParseService {

	private final ArticleParseStrategyFactory articleParseStrategyFactory;
	private final LastArticleCacheService lastArticleCacheService;
	private final ArticleService articleService;

	public List<ParsedArticle> parseArticles(Source source) throws CustomException {
		log.debug("[{}] 아티클 수집 시작", source.getName());

		ArticleParseStrategy strategy = articleParseStrategyFactory.getStrategy(source.getParseMethod());

		List<ParsedArticle> parsedArticles = strategy.process(source);

		if (parsedArticles.isEmpty()) {
			log.debug("[{}] 파싱된 아티클이 없습니다", source.getName());
			return parsedArticles;
		}

		List<ParsedArticle> filteredByDate = afterPublishedDateFilter(parsedArticles, source.getId());
		List<ParsedArticle> filterdByLink = existLinkFilter(filteredByDate, source.getId());

		log.debug("[{}] 필터링 후 {}개 아티클 수집 완료", source.getName(), filterdByLink.size());
		return filterdByLink;
	}

	/**
	 * 마지막 발행일자 필터
	 * @param articles 필터링랑 아티클 리스트
	 * @param sourceId 해당 소스 ID
	 * @return 필터링된 아티클 리스트
	 */
	private List<ParsedArticle> afterPublishedDateFilter(List<ParsedArticle> articles, Long sourceId) {
		Date lastPublishedDate = lastArticleCacheService.getLastPublishedDate(sourceId);

		return articles.stream()
			.filter(article -> article.getPublishedAt().after(lastPublishedDate) ||
				article.getPublishedAt().equals(lastPublishedDate))
			.toList();
	}

	/**
	 * 링크 중복 필터
	 * @param articles 필터링할 아티클 리스트
	 * @param sourceId 해당 소스 ID
	 * @return 필터링된 아티클 리스트
	 */
	private List<ParsedArticle> existLinkFilter(List<ParsedArticle> articles, Long sourceId) {
		Set<String> links = articles.stream()
			.map(ParsedArticle::getLink)
			.collect(Collectors.toSet());

		Set<String> existsLinks = articleService.getArticlesByLink(links);

		return articles.stream()
			.filter(a -> !existsLinks.contains(a.getLink()))
			.toList();
	}
}
