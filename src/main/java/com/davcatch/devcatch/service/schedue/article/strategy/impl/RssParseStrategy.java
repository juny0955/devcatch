package com.davcatch.devcatch.service.schedue.article.strategy.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.ParseMethod;
import com.davcatch.devcatch.domain.Source;
import com.davcatch.devcatch.exception.CustomException;
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
public class RssParseStrategy extends AbstractArticleStrategy {

	public RssParseStrategy(RssReader rssReader, ContentExtractorFactory contentExtractorFactory) {
		super(rssReader, contentExtractorFactory);
	}

	@Override
	public List<ParsedArticle> process(Source source) throws CustomException {
		Optional<SyndFeed> optionalFeed = getRssFeed(source);
		if (optionalFeed.isEmpty()) {
			log.warn("[{}] RSS 피드가 존재하지 않습니다.", source.getName());
			return Collections.emptyList();
		}

		List<ParsedArticle> parsedArticles = new ArrayList<>();
		for (SyndEntry entry : optionalFeed.get().getEntries()) {
			ContentExtractor extractor = getContentExtractor(source.getParseMethod());
			String content = extractor.extractContent(entry, null);

			parsedArticles.add(ParsedArticle.of(content, entry));
		}

		return parsedArticles;
	}

	@Override
	public boolean supports(ParseMethod parseMethod) {
		return parseMethod == ParseMethod.RSS_CONTENT || parseMethod == ParseMethod.RSS_DESCRIPTION;
	}
}
