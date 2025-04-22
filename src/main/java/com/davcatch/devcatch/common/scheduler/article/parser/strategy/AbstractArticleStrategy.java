package com.davcatch.devcatch.common.scheduler.article.parser.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.davcatch.devcatch.common.integration.selenium.SeleniumBrowserService;
import com.davcatch.devcatch.common.scheduler.article.dto.ParsedArticle;
import com.davcatch.devcatch.domain.source.ParseMethod;
import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.integration.rss.RssReaderService;
import com.davcatch.devcatch.common.scheduler.article.extractor.strategy.ContentExtractorStrategy;
import com.davcatch.devcatch.common.scheduler.article.extractor.factory.ContentExtractorFactory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractArticleStrategy implements ArticleParseStrategy {

	private final RssReaderService rssReaderService;
	private final SeleniumBrowserService seleniumBrowserService;
	private final ContentExtractorFactory contentExtractorFactory;

	@Override
	public List<ParsedArticle> process(Source source) throws CustomException {
		ContentExtractorStrategy contentExtractor = getContentExtractor(source.getParseMethod());

		List<SyndEntry> entries = fetchEntries(source);

		if (entries.isEmpty()) {
			return Collections.emptyList();
		}

		List<ParsedArticle> parsedArticles = new ArrayList<>();
		for (SyndEntry entry : entries) {
			try {
				ParsedArticle article = processEntry(entry, source, contentExtractor);
				if (article != null) {
					parsedArticles.add(article);
				}
			} catch (Exception e) {
				log.error("[{}] 엔트리 처리 중 오류 발생: {}", source.getName(), e.getMessage());
			}
		}

		log.debug("[{}] 총 {}개 아티클 파싱 완료", source.getName(), parsedArticles.size());
		return parsedArticles;
	}

	protected abstract List<SyndEntry> fetchEntries(Source source);
	protected abstract ParsedArticle processEntry(SyndEntry entry, Source source, ContentExtractorStrategy contentExtractor) throws CustomException;

	protected List<SyndEntry> getEntriesFromRss(Source source) {
		return rssReaderService.reader(source)
			.map(SyndFeed::getEntries)
			.orElseGet(() -> {
				log.warn("[{}] RSS 피드가 존재하지 않습니다.", source.getName());
				return Collections.emptyList();
			});
	}

	protected List<SyndEntry> getEntriesFromHeadless(Source source) {
		return seleniumBrowserService.reader(source)
			.map(SyndFeed::getEntries)
			.orElseGet(() -> {
				log.warn("[{}] RSS 피드가 존재하지 않습니다", source.getName());
				return Collections.emptyList();
			});
	}

	protected ContentExtractorStrategy getContentExtractor(ParseMethod parseMethod) throws CustomException {
		return contentExtractorFactory.getExtractor(parseMethod);
	}
}
