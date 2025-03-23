package com.davcatch.devcatch.schedue.article.parser.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.source.ParseMethod;
import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;
import com.davcatch.devcatch.schedue.article.parser.strategy.ArticleParseStrategy;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
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

		if (strategy == null) {
			log.error("파싱 메서드 {}에 대한 전략을 찾지 못함", parseMethod);
			throw new CustomException(ErrorCode.NO_SUPPORTS_STRATEGY);
		}

		return strategy;
	}
}
