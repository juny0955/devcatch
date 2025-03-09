package com.davcatch.devcatch.service.schedue.article.strategy;

import java.util.List;

import com.davcatch.devcatch.domain.ParseMethod;
import com.davcatch.devcatch.domain.Source;
import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.service.schedue.article.dto.ParsedArticle;

public interface ArticleParseStrategy {

	/**
	 * Article Parsing
	 * @param source 해당 Source
	 * @return 파싱된 Article List
	 * @throws CustomException
	 */
	List<ParsedArticle> process(Source source) throws CustomException;

	boolean supports(ParseMethod parseMethod);
}
