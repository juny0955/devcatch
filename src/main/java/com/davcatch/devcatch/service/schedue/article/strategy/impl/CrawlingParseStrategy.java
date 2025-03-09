package com.davcatch.devcatch.service.schedue.article.strategy.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.ParseMethod;
import com.davcatch.devcatch.domain.Source;
import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.integration.crawling.WebCrawler;
import com.davcatch.devcatch.integration.rss.RssReader;
import com.davcatch.devcatch.service.schedue.article.dto.ParsedArticle;
import com.davcatch.devcatch.service.schedue.article.extractor.ContentExtractor;
import com.davcatch.devcatch.service.schedue.article.extractor.ContentExtractorFactory;
import com.davcatch.devcatch.service.schedue.article.strategy.AbstractArticleStrategy;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CrawlingParseStrategy extends AbstractArticleStrategy {

	private final static int MAX_PARSE_PAGE = 3;
	private final WebCrawler webCrawler;

	public CrawlingParseStrategy(RssReader rssReader, WebCrawler webCrawler,
		ContentExtractorFactory contentExtractorFactory) {
		super(rssReader, contentExtractorFactory);
		this.webCrawler = webCrawler;
	}

	@Override
	public List<ParsedArticle> process(Source source) throws CustomException {
		Optional<SyndFeed> optionalFeed = getRssFeed(source);
		if (optionalFeed.isEmpty()) {
			log.warn("[{}] RSS 피드가 존재하지 않습니다.", source.getName());
			return Collections.emptyList();
		}

		List<ParsedArticle> parsedArticles = new ArrayList<>();
		List<SyndEntry> entries = optionalFeed.get().getEntries();
		for (int i = 0; i < MAX_PARSE_PAGE ; i++) {
			SyndEntry entry = entries.get(i);
			Optional<Document> optioanlDocument = webCrawler.getDocument(entry.getLink());
			if (optioanlDocument.isEmpty())
				continue;

			Document document = optioanlDocument.get();

			ContentExtractor extractor = getContentExtractor(source.getParseMethod());
			String content = extractor.extractContent(null, document);

			parsedArticles.add(ParsedArticle.of(content, entry, source.isUseLink()));
		}

		if (parsedArticles.isEmpty())
			return Collections.emptyList();

		return parsedArticles;
	}

	@Override
	public boolean supports(ParseMethod parseMethod) {
		return parseMethod == ParseMethod.CRAWLING;
	}
}
