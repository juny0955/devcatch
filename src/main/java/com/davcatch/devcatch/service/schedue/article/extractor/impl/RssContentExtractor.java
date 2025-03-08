package com.davcatch.devcatch.service.schedue.article.extractor.impl;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.ParseMethod;
import com.davcatch.devcatch.service.schedue.article.extractor.ContentExtractor;
import com.rometools.rome.feed.synd.SyndEntry;

@Component
public class RssContentExtractor implements ContentExtractor {

	@Override
	public String extractContent(SyndEntry entry, Document document) {
		return entry.getContents().get(0).getValue();
	}

	@Override
	public boolean supports(ParseMethod parseMethod) {
		return parseMethod == ParseMethod.RSS_CONTENT;
	}
}
