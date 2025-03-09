package com.davcatch.devcatch.service.schedue.article.extractor.impl;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.ParseMethod;
import com.davcatch.devcatch.service.schedue.article.extractor.ContentExtractor;
import com.rometools.rome.feed.synd.SyndEntry;

@Component
public class CawlingBodyExtractor implements ContentExtractor {

	@Override
	public String extractContent(SyndEntry entry, Document document) {
		return document.body().text();
	}

	@Override
	public boolean supports(ParseMethod parseMethod) {
		return parseMethod == ParseMethod.CRAWLING;
	}
}
