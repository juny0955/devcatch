package com.davcatch.devcatch.schedue.article.parser.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.source.ParseMethod;
import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.integration.rss.RssReader;
import com.davcatch.devcatch.schedue.article.extractor.factory.ContentExtractorFactory;
import com.davcatch.devcatch.schedue.article.dto.ParsedArticle;
import com.davcatch.devcatch.schedue.article.extractor.strategy.ContentExtractorStrategy;
import com.davcatch.devcatch.schedue.article.strategy.AbstractArticleStrategy;
import com.rometools.rome.feed.synd.SyndEntry;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RssParseStrategy extends AbstractArticleStrategy {

	public RssParseStrategy(RssReader rssReader, ContentExtractorFactory contentExtractorFactory) {
		super(rssReader, contentExtractorFactory);
	}

	@Override
	public List<ParsedArticle> process(Source source) throws CustomException {
		ContentExtractorStrategy extractor = getContentExtractor(source.getParseMethod());
		List<SyndEntry> entries = getEntries(source);

		List<ParsedArticle> parsedArticles = new ArrayList<>();
		for (SyndEntry entry : entries) {
			String content = extractor.extractContent(entry, null);

			parsedArticles.add(ParsedArticle.of(content, entry, source.isUseLink()));
		}

		return parsedArticles;
	}

	@Override
	public Set<ParseMethod> getSupportedParseMethods() {
		return Set.of(ParseMethod.RSS_DESCRIPTION, ParseMethod.RSS_CONTENT);
	}
}
