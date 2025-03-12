package com.davcatch.devcatch.service.schedue.article.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.ParseMethod;
import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.exception.ErrorCode;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleParseStrategyFactory {

	private final List<ArticleParseStrategy> articleParseStrategies;
	private Map<ParseMethod, ArticleParseStrategy> strategyMap;

	@PostConstruct
	public void init() {
		strategyMap = new HashMap<>();
		for (ArticleParseStrategy strategy : articleParseStrategies) {
			for (ParseMethod method : strategy.getSupportedParseMethods())
				strategyMap.put(method, strategy);
		}
	}

	public ArticleParseStrategy getStrategy(ParseMethod parseMethod) throws CustomException {
		ArticleParseStrategy strategy = strategyMap.get(parseMethod);

		if (strategy == null)
			throw new CustomException(ErrorCode.NO_SUPPORTS_STRATEGY);

		return strategy;
	}
}
