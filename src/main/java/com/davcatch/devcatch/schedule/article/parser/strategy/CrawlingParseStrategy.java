package com.davcatch.devcatch.schedule.article.parser.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.domain.source.ParseMethod;
import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.integration.crawling.WebCrawler;
import com.davcatch.devcatch.integration.rss.RssReaderService;
import com.davcatch.devcatch.schedule.article.dto.ParsedArticle;
import com.davcatch.devcatch.schedule.article.extractor.factory.ContentExtractorFactory;
import com.davcatch.devcatch.schedule.article.extractor.strategy.ContentExtractorStrategy;
import com.rometools.rome.feed.synd.SyndEntry;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CrawlingParseStrategy extends AbstractArticleStrategy {

	private final static int MAX_PARSE_PAGE = 3;
	private final WebCrawler webCrawler;

	public CrawlingParseStrategy(RssReaderService rssReaderService, WebCrawler webCrawler, ContentExtractorFactory contentExtractorFactory) {
		super(rssReaderService, contentExtractorFactory);
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
		return Set.of(ParseMethod.CRAWLING);
	}
}
