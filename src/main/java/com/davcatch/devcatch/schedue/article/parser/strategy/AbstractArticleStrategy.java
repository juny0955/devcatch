package com.davcatch.devcatch.schedue.article.parser.strategy;

import java.util.Collections;
import java.util.List;

import com.davcatch.devcatch.domain.source.ParseMethod;
import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.integration.rss.RssReaderService;
import com.davcatch.devcatch.schedue.article.extractor.strategy.ContentExtractorStrategy;
import com.davcatch.devcatch.schedue.article.extractor.factory.ContentExtractorFactory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractArticleStrategy implements ArticleParseStrategy {

	private final RssReaderService rssReaderService;
	private final ContentExtractorFactory contentExtractorFactory;

	protected List<SyndEntry> getEntries(Source source) {
		return rssReaderService.reader(source.getFeedUrl())
			.map(SyndFeed::getEntries)
			.orElseGet(() -> {
				log.warn("[{}] RSS 피드가 존재하지 않습니다.", source.getName());
				return Collections.emptyList();
			});
	}

	protected ContentExtractorStrategy getContentExtractor(ParseMethod parseMethod) throws CustomException {
		return contentExtractorFactory.getExtractor(parseMethod);
	}
}
