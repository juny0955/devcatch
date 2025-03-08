package com.davcatch.devcatch.service.schedue.article.strategy;

import java.util.Optional;

import com.davcatch.devcatch.domain.ParseMethod;
import com.davcatch.devcatch.domain.Source;
import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.integration.rss.RssReader;
import com.davcatch.devcatch.service.schedue.article.extractor.ContentExtractor;
import com.davcatch.devcatch.service.schedue.article.extractor.ContentExtractorFactory;
import com.rometools.rome.feed.synd.SyndFeed;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractArticleStrategy implements ArticleParseStrategy{

	private final RssReader rssReader;
	private final ContentExtractorFactory contentExtractorFactory;

	protected Optional<SyndFeed> getRssFeed(Source source) {
		return rssReader.reader(source.getFeedUrl());
	}

	protected ContentExtractor getContentExtractor(ParseMethod parseMethod) throws CustomException {
		return contentExtractorFactory.getExtractor(parseMethod);
	}
}
