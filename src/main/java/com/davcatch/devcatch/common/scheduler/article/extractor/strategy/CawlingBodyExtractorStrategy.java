package com.davcatch.devcatch.scheduler.article.extractor.strategy;

import java.util.Set;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.source.ParseMethod;
import com.rometools.rome.feed.synd.SyndEntry;

@Component
public class CawlingBodyExtractorStrategy implements ContentExtractorStrategy {

	@Override
	public String extractContent(SyndEntry entry, Document document) {
		return document.body().text();
	}

	@Override
	public Set<ParseMethod> getSupportedParseMethod() {
		return Set.of(ParseMethod.CRAWLING);
	}
}
