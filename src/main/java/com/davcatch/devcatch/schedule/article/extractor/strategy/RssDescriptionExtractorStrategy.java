package com.davcatch.devcatch.schedule.article.extractor.strategy;

import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.source.ParseMethod;
import com.rometools.rome.feed.synd.SyndEntry;

@Component
public class RssDescriptionExtractorStrategy implements ContentExtractorStrategy {

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
