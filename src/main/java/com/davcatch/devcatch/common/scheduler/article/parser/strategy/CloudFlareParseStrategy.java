package com.davcatch.devcatch.common.scheduler.article.parser.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.davcatch.devcatch.common.exception.CustomException;
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
public class CloudFlareParseStrategy extends AbstractArticleStrategy {

	public CloudFlareParseStrategy(RssReaderService rssReaderService, SeleniumBrowserService seleniumBrowserService, ContentExtractorFactory contentExtractorFactory) {
		super(rssReaderService, seleniumBrowserService, contentExtractorFactory);
	}

	@Override
	public List<ParsedArticle> process(Source source) throws CustomException {
		ContentExtractorStrategy extractor = getContentExtractor(source.getParseMethod());
		List<SyndEntry> entries = getEntriesFromHeadless(source);

		List<ParsedArticle> parsedArticles = new ArrayList<>();
		for (SyndEntry entry : entries) {
			String content = extractor.extractContent(entry, null);
			parsedArticles.add(ParsedArticle.of(entry, source, content));
		}

		return parsedArticles;
	}

	@Override
	public Set<ParseMethod> getSupportedParseMethods() {
		return Set.of(ParseMethod.CLOUDFLARE_RSS_CONTENT);
	}
}
