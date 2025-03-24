package com.davcatch.devcatch.common.scheduler.article.parser.strategy;

import java.util.List;
import java.util.Set;

import com.davcatch.devcatch.domain.source.ParseMethod;
import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.scheduler.article.dto.ParsedArticle;

public interface ArticleParseStrategy {

	/**
	 * 아티클 파싱
	 *
	 * @param source 해당 Source
	 * @return 파싱된 아티클 목록
	 * @throws CustomException 파싱중 오류 발생 시
	 */
	List<ParsedArticle> process(Source source) throws CustomException;

	/**
	 * 지원하는 파싱 방식 목록
	 *
	 * @return 파싱 방식 목록
	 */
	Set<ParseMethod> getSupportedParseMethods();
}
