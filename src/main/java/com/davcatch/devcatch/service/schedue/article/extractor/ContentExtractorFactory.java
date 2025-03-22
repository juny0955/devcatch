package com.davcatch.devcatch.service.schedue.article.extractor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.source.ParseMethod;
import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContentExtractorFactory {

	private final List<ContentExtractor> contentExtractors;
	private Map<ParseMethod, ContentExtractor> extractorMap;

	@PostConstruct
	public void init() {
		extractorMap = new HashMap<>();
		for (ContentExtractor contentExtractor : contentExtractors) {
			for (ParseMethod parseMethod : contentExtractor.getSupportedParseMethod())
				extractorMap.put(parseMethod, contentExtractor);

		}
	}

	public ContentExtractor getExtractor(ParseMethod parseMethod) throws CustomException {
		ContentExtractor contentExtractor = extractorMap.get(parseMethod);

		if (contentExtractor == null)
			throw new CustomException(ErrorCode.NO_SUPPORTS_STRATEGY);

		return contentExtractor;
	}
}
