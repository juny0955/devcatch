package com.davcatch.devcatch.common.integration.crawling;

import java.io.IOException;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebCrawler {

	private final RestClient crawlingRestClient;

	/**
	 * 크롤링
	 * @param link site link
	 * @return Document
	 */
	public Optional<Document> getDocument(String link) {
		log.debug("크롤링 시작 : {}", link);

		Document document = null;
		try {
			String html = crawlingRestClient.get()
				.uri(link)
				.retrieve()
				.body(String.class);

			if (html == null || html.isBlank()) {
				log.warn("[{}] 해당 페이지를 가져올 수 없습니다", link);
				return Optional.empty();
			}

			document = Jsoup.parse(html);
			log.debug("크롤링 정상 수집 : {}", link);
		} catch (Exception e) {
			log.error("({}) 크롤링 중 에러 발생 : {}", link, e.getMessage());
		}

		return Optional.ofNullable(document);
	}
}
