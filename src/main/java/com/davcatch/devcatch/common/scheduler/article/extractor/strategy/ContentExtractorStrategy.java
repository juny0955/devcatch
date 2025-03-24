package com.davcatch.devcatch.common.scheduler.article.extractor.strategy;

import java.util.Set;

import org.jsoup.nodes.Document;

import com.davcatch.devcatch.domain.source.ParseMethod;
import com.rometools.rome.feed.synd.SyndEntry;

public interface ContentExtractorStrategy {

	/**
	 * 본문 내용 파싱
	 * @param entry RSS 파싱 시
	 * @param document 크롤링 시
	 * @return 본문 내용 (HTML 태그 제거)
	 */
	String extractContent(SyndEntry entry, Document document);

	Set<ParseMethod> getSupportedParseMethod();
}
