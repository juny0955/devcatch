package com.davcatch.devcatch.service.schedue.article.extractor.impl;

import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.ParseMethod;
import com.davcatch.devcatch.service.schedue.article.extractor.ContentExtractor;
import com.rometools.rome.feed.synd.SyndEntry;

@Component
public class RssDescriptionExtractor implements ContentExtractor {

	@Override
	public String extractContent(SyndEntry entry, Document document) {
		Document doc = Jsoup.parse(entry.getDescription().getValue());
		return doc.text();
	}

	@Override
	public Set<ParseMethod> getSupportedParseMethod() {
		return Set.of(ParseMethod.RSS_DESCRIPTION);
	}
}
