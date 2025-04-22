package com.davcatch.devcatch.common.scheduler.article.parser.strategy;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.davcatch.devcatch.common.integration.rss.RssReaderService;
import com.davcatch.devcatch.common.integration.selenium.SeleniumBrowserService;
import com.davcatch.devcatch.common.scheduler.article.dto.ParsedArticle;
import com.davcatch.devcatch.common.scheduler.article.extractor.factory.ContentExtractorFactory;
import com.davcatch.devcatch.common.scheduler.article.extractor.strategy.ContentExtractorStrategy;
import com.davcatch.devcatch.domain.source.ParseMethod;
import com.davcatch.devcatch.domain.source.Source;
import com.rometools.rome.feed.synd.SyndEntry;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RssParseStrategy extends AbstractArticleStrategy {

	public RssParseStrategy(RssReaderService rssReaderService, SeleniumBrowserService seleniumBrowserService, ContentExtractorFactory contentExtractorFactory) {
		super(rssReaderService, seleniumBrowserService, contentExtractorFactory);
	}

	@Override
	protected List<SyndEntry> fetchEntries(Source source) {
		return getEntriesFromRss(source);
	}

	@Override
	protected ParsedArticle processEntry(SyndEntry entry, Source source, ContentExtractorStrategy contentExtractor) {
		String content = contentExtractor.extractContent(entry, null);
		return ParsedArticle.of(entry, source, content);
	}

	@Override
	public Set<ParseMethod> getSupportedParseMethods() {
		return Set.of(ParseMethod.RSS_DESCRIPTION, ParseMethod.RSS_CONTENT);
	}
}
