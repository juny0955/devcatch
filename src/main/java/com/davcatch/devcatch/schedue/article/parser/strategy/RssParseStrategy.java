package com.davcatch.devcatch.schedue.article.parser.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.source.ParseMethod;
import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.integration.rss.RssReaderService;
import com.davcatch.devcatch.schedue.article.extractor.factory.ContentExtractorFactory;
import com.davcatch.devcatch.schedue.article.dto.ParsedArticle;
import com.davcatch.devcatch.schedue.article.extractor.strategy.ContentExtractorStrategy;
import com.rometools.rome.feed.synd.SyndEntry;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RssParseStrategy extends AbstractArticleStrategy {

	public RssParseStrategy(RssReaderService rssReaderService, ContentExtractorFactory contentExtractorFactory) {
		super(rssReaderService, contentExtractorFactory);
	}

	@Override
	public List<ParsedArticle> process(Source source) throws CustomException {
		ContentExtractorStrategy extractor = getContentExtractor(source.getParseMethod());
		List<SyndEntry> entries = getEntries(source);

		List<ParsedArticle> parsedArticles = new ArrayList<>();
		for (SyndEntry entry : entries) {
			String content = extractor.extractContent(entry, null);

			ParsedArticle parsedArticle = ParsedArticle.builder()
					.title(entry.getTitle())
					.link(source.isUseLink() ? entry.getLink() : entry.getUri())
					.content(content)
					.publishedAt(entry.getPublishedDate() != null ? entry.getPublishedDate() : entry.getUpdatedDate())
					.build();

			parsedArticles.add(parsedArticle);
		}

		return parsedArticles;
	}

	@Override
	public Set<ParseMethod> getSupportedParseMethods() {
		return Set.of(ParseMethod.RSS_DESCRIPTION, ParseMethod.RSS_CONTENT);
	}
}
