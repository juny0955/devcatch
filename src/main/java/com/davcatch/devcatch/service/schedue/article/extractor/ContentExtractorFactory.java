package com.davcatch.devcatch.service.schedue.article.extractor;

import java.util.List;

import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.ParseMethod;
import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContentExtractorFactory {

	private final List<ContentExtractor> contentExtractors;

	public ContentExtractor getExtractor(ParseMethod parseMethod) throws CustomException {
		return contentExtractors.stream()
			.filter(contentExtractor -> contentExtractor.supports(parseMethod))
			.findFirst()
			.orElseThrow(() -> new CustomException(ErrorCode.NO_SUPPORTS_STRATEGY));
	}
}
