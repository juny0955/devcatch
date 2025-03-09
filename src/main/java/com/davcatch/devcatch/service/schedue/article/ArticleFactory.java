package com.davcatch.devcatch.service.schedue.article;

import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.Article;
import com.davcatch.devcatch.domain.Source;
import com.davcatch.devcatch.integration.gpt.GptSummaryService;
import com.davcatch.devcatch.integration.gpt.response.GptResponse;
import com.davcatch.devcatch.service.schedue.article.dto.ParsedArticle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleFactory {

	private final GptSummaryService gptSummaryService;

	/**
	 * RSS 엔트리를 기반으로 Article 객체를 생성한다.
	 * GPT 요약 서비스를 활용하여 요약 정보를 포함시킨다.
	 *
	 * @param source 해당 Source
	 */
	public Article createArticle(Source source, ParsedArticle parsedArticle) {
		try {
			GptResponse response = gptSummaryService.getSummary(parsedArticle.getContent());

			return Article.of(source, parsedArticle, response);
		} catch (Exception e) {
			log.error("Article 생성 중 오류 발생: {}", e.getMessage());
			return null;
		}
	}
}
