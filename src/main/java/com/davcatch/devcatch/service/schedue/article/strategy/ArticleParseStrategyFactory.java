package com.davcatch.devcatch.service.schedue.article.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.ParseMethod;
import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleParseStrategyFactory {

	private final List<ArticleParseStrategy> articleParseStrategies;

	public ArticleParseStrategy getStrategy(ParseMethod parseMethod) throws CustomException {
		return articleParseStrategies.stream()
			.filter(articleParseStrategy -> articleParseStrategy.supports(parseMethod))
			.findFirst()
			.orElseThrow(() -> new CustomException(ErrorCode.NO_SUPPORTS_STRATEGY));
	}
}
