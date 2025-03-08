package com.davcatch.devcatch.service.schedue.article.strategy;

import java.util.List;

import com.davcatch.devcatch.domain.ParseMethod;
import com.davcatch.devcatch.domain.Source;
import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.service.schedue.article.dto.ParsedArticle;

public interface ArticleParseStrategy {
	List<ParsedArticle> process(Source source) throws CustomException;

	boolean supports(ParseMethod parseMethod);
}
