package com.davcatch.devcatch.service.schedue.article;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.Article;
import com.davcatch.devcatch.domain.Source;
import com.davcatch.devcatch.integration.gpt.GptSummaryService;
import com.davcatch.devcatch.integration.gpt.response.GptResponse;
import com.rometools.rome.feed.synd.SyndEntry;

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
	 * @param entry 필터링된 feed
	 */
	public Article createArticle(Source source, SyndEntry entry) {
		try {
			String content = removeHtmlTags(entry.getContents().get(0).getValue());
			GptResponse response = gptSummaryService.getSummary(content);
			return Article.of(source, entry, response);
		} catch (Exception e) {
			log.error("Article 생성 중 오류 발생: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * HTML 태그 제거
	 * Jsoup 라이브러리 사용해 HTML 태그 전체 제거
	 * 깔끔한 본문만 반환
	 *
	 * @param rawContent HTML 태그가 포함된 feed 내용
	 * @return HTML 태그 제거한 내용
	 */
	private String removeHtmlTags(String rawContent) {
		Document doc = Jsoup.parse(rawContent);
		return doc.text();
	}
}
