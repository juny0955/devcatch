package com.davcatch.devcatch.service.schedue.article;

import org.springframework.stereotype.Service;

import com.davcatch.devcatch.domain.Article;
import com.davcatch.devcatch.domain.Source;
import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.integration.gpt.GptSummaryService;
import com.davcatch.devcatch.integration.gpt.response.GptResponse;
import com.rometools.rome.feed.synd.SyndEntry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleFactory {

	private final GptSummaryService gptSummaryService;

	/**
	 * RSS 엔트리를 기반으로 Article 객체를 생성한다.
	 * GPT 요약 서비스를 활용하여 요약 정보를 포함시킨다.
	 */
	public Article createArticle(Source source, SyndEntry entry) throws CustomException {
		try {
			String content = entry.getContents().get(0).getValue();
			GptResponse response = gptSummaryService.getSummary(content);
			return Article.from(source, entry, response);
		} catch (Exception e) {
			log.error("Article 생성 중 오류 발생: {}", e.getMessage());
			return null;
		}
	}
}
