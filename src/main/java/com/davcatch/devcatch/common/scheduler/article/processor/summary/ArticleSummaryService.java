package com.davcatch.devcatch.common.scheduler.article.processor.summary;

import org.springframework.stereotype.Service;

import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.scheduler.article.ContentParser;
import com.davcatch.devcatch.common.scheduler.article.dto.ArticleSummary;
import com.davcatch.devcatch.common.scheduler.article.dto.Content;
import com.davcatch.devcatch.common.integration.gpt.GptSummaryService;
import com.davcatch.devcatch.common.integration.gpt.response.GptResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleSummaryService {

	private final GptSummaryService gptSummaryService;
	private final ContentParser contentParser;

	public ArticleSummary summarizeArticle(String content) throws CustomException {
		log.debug("아티클 요약 시작");

		GptResponse gptResponse = gptSummaryService.getSummary(content);
		Content parseContent = contentParser.parseContent(gptResponse);
		ArticleSummary summary = ArticleSummary.of(parseContent.getSummary(), parseContent.getTag());

		log.debug("아티클 요약 완료");
		return summary;
	}
}
