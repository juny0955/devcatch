package com.davcatch.devcatch.common.scheduler.article.parser.strategy;

import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;
import com.davcatch.devcatch.common.integration.crawling.WebCrawler;
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
public class CrawlingParseStrategy extends AbstractArticleStrategy {

	private final static int MAX_PARSE_PAGE = 3;
	private final WebCrawler webCrawler;

	public CrawlingParseStrategy(RssReaderService rssReaderService, SeleniumBrowserService seleniumBrowserService, ContentExtractorFactory contentExtractorFactory, WebCrawler webCrawler) {
		super(rssReaderService, seleniumBrowserService, contentExtractorFactory);
		this.webCrawler = webCrawler;
	}

	@Override
	protected List<SyndEntry> fetchEntries(Source source) {
		List<SyndEntry> entries = getEntriesFromRss(source);
		return entries.subList(0, Math.min(MAX_PARSE_PAGE, entries.size()));
	}

	@Override
	protected ParsedArticle processEntry(SyndEntry entry, Source source, ContentExtractorStrategy contentExtractor) throws CustomException {
		String link = source.isUseLink() ? entry.getLink() : entry.getUri();
		Document document = webCrawler.getDocument(link)
			.orElseThrow(() -> new CustomException(ErrorCode.CONTENT_PARSE_ERROR));

		String content = contentExtractor.extractContent(null, document);
		return ParsedArticle.of(entry, source, content);
	}

	@Override
	public Set<ParseMethod> getSupportedParseMethods() {
		return Set.of(ParseMethod.CRAWLING);
	}
}
