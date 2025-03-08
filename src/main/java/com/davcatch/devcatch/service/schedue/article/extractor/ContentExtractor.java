package com.davcatch.devcatch.service.schedue.article.extractor;

import org.jsoup.nodes.Document;

import com.davcatch.devcatch.domain.ParseMethod;
import com.rometools.rome.feed.synd.SyndEntry;

public interface ContentExtractor {

	String extractContent(SyndEntry entry, Document document);

	boolean supports(ParseMethod parseMethod);
}
