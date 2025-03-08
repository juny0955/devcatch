package com.davcatch.devcatch.service.schedue.article.strategy.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.ParseMethod;
import com.davcatch.devcatch.domain.Source;
import com.davcatch.devcatch.integration.crawling.WebCrawler;
import com.davcatch.devcatch.service.schedue.article.dto.ParsedArticle;
import com.davcatch.devcatch.service.schedue.article.extractor.ContentExtractorFactory;
import com.davcatch.devcatch.service.schedue.article.strategy.ArticleParseStrategy;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CrawlingParseStrategy implements ArticleParseStrategy {

	private final WebCrawler webCrawler;
	private final ContentExtractorFactory contentExtractorFactory;

	@Override
	public List<ParsedArticle> process(Source source) {
		return List.of();
	}

	@Override
	public boolean supports(ParseMethod parseMethod) {
		return parseMethod == ParseMethod.CRAWLING;
	}
}
