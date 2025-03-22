package com.davcatch.devcatch.schedue.article.parser.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.source.ParseMethod;
import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.integration.crawling.WebCrawler;
import com.davcatch.devcatch.integration.rss.RssReader;
import com.davcatch.devcatch.schedue.article.extractor.factory.ContentExtractorFactory;
import com.davcatch.devcatch.schedue.article.dto.ParsedArticle;
import com.davcatch.devcatch.schedue.article.extractor.strategy.ContentExtractorStrategy;
import com.davcatch.devcatch.schedue.article.strategy.AbstractArticleStrategy;
import com.rometools.rome.feed.synd.SyndEntry;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CrawlingParseStrategy extends AbstractArticleStrategy {

	private final static int MAX_PARSE_PAGE = 3;
	private final WebCrawler webCrawler;

	public CrawlingParseStrategy(RssReader rssReader, WebCrawler webCrawler, ContentExtractorFactory contentExtractorFactory) {
		super(rssReader, contentExtractorFactory);
		this.webCrawler = webCrawler;
	}

	@Override
	public List<ParsedArticle> process(Source source) throws CustomException {
		ContentExtractorStrategy extractor = getContentExtractor(source.getParseMethod());
		List<SyndEntry> entries = getEntries(source);

		List<ParsedArticle> parsedArticles = new ArrayList<>();
		for (int i = 0; i < Math.min(MAX_PARSE_PAGE, entries.size()) ; i++) {
			SyndEntry entry = entries.get(i);

			Document document = webCrawler.getDocument(entry.getLink()).orElse(null);
			if (document == null)
				continue;

			String content = extractor.extractContent(null, document);
			parsedArticles.add(ParsedArticle.of(content, entry, source.isUseLink()));
		}

		if (parsedArticles.isEmpty())
			return Collections.emptyList();

		return parsedArticles;
	}

	@Override
	public Set<ParseMethod> getSupportedParseMethods() {
		return Set.of(ParseMethod.CRAWLING);
	}
}
