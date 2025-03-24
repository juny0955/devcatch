package com.davcatch.devcatch.schedule.article.parser;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.repository.article.ArticleRepository;
import com.davcatch.devcatch.schedule.article.dto.ParsedArticle;
import com.davcatch.devcatch.schedule.article.parser.factory.ArticleParseStrategyFactory;
import com.davcatch.devcatch.schedule.article.parser.strategy.ArticleParseStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleParseService {

	private final ArticleParseStrategyFactory articleParseStrategyFactory;
	private final ArticleRepository articleRepository;

	public List<ParsedArticle> parseArticles(Source source) throws CustomException {
		log.debug("[{}] 아티클 수집 시작", source.getName());

		ArticleParseStrategy strategy = articleParseStrategyFactory.getStrategy(source.getParseMethod());

		List<ParsedArticle> process = strategy.process(source);

		if (process.isEmpty()) {
			log.debug("[{}] 파싱된 아티클이 없습니다", source.getName());
			return process;
		}

		List<ParsedArticle> filteredArticles = filterArticles(process, source);

		log.debug("[{}] 필터링 후 {}개 아티클 수집 완료", source.getName(), filteredArticles.size());
		return filteredArticles;

	}

	/**
	 * 아티클 필터링
	 * - DB에 존재하는 아티클 제외
	 * - 기준 날짜 이전 아티클 제외
	 *
	 * @param articles 파싱된 아티클
	 * @param source 해당 소스
	 * @return 필터링된 아티클 목록
	 */
	private List<ParsedArticle> filterArticles(List<ParsedArticle> articles, Source source) {
		Date cutoffDate = getArticleCutoffDate(source);

		List<ParsedArticle> dateFilteredArticles = articles.stream()
			.filter(article -> article.getPublishedAt().after(cutoffDate) ||
								article.getPublishedAt().equals(cutoffDate))
			.toList();

		if (dateFilteredArticles.isEmpty())
			return dateFilteredArticles;

		Set<String> allLinks = dateFilteredArticles.stream()
			.map(ParsedArticle::getLink)
			.collect(Collectors.toSet());

		Set<String> existsLinks = articleRepository.findExistsLinks(allLinks);

		return dateFilteredArticles.stream()
			.filter(article -> !existsLinks.contains(article.getLink()))
			.toList();
	}

	/**
	 * 아티클 수집 날짜 찾기
	 * @param source 해당 소스
	 * @return 수집 날짜
	 */
	private Date getArticleCutoffDate(Source source) {
		Optional<Article> lastPublishedArticle = articleRepository.findLastPublishedArticle(source.getId());

		if (lastPublishedArticle.isEmpty()) {
			LocalDate yesterDay = LocalDate.now().minusDays(1);
			return Date.from(yesterDay.atStartOfDay(ZoneId.systemDefault()).toInstant());
		}

		return lastPublishedArticle.get().getPublishedAt();
	}
}
