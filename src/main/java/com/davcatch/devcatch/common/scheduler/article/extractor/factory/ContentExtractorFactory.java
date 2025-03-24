package com.davcatch.devcatch.common.scheduler.article.extractor.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.davcatch.devcatch.common.scheduler.article.extractor.strategy.ContentExtractorStrategy;
import com.davcatch.devcatch.domain.source.ParseMethod;
import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContentExtractorFactory {

	private final List<ContentExtractorStrategy> contentExtractorStrategies;
	private Map<ParseMethod, ContentExtractorStrategy> extractorMap;

	@PostConstruct
	public void init() {
		extractorMap = new HashMap<>();
		for (ContentExtractorStrategy contentExtractorStrategy : contentExtractorStrategies) {
			for (ParseMethod parseMethod : contentExtractorStrategy.getSupportedParseMethod())
				extractorMap.put(parseMethod, contentExtractorStrategy);

		}
	}

	public ContentExtractorStrategy getExtractor(ParseMethod parseMethod) throws CustomException {
		ContentExtractorStrategy contentExtractorStrategy = extractorMap.get(parseMethod);

		if (contentExtractorStrategy == null)
			throw new CustomException(ErrorCode.NO_SUPPORTS_STRATEGY);

		return contentExtractorStrategy;
	}
}
