package com.davcatch.devcatch.common.integration.crawling;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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

		try {
			byte[] bytes = crawlingRestClient.get()
					.uri(link)
					.retrieve()
					.body(byte[].class);

			if (bytes == null || bytes.length == 0) {
				log.warn("[{}] 해당 페이지를 가져올 수 없습니다", link);
				return Optional.empty();
			}

			try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
				Document doc = Jsoup.parse(bais, null, link, Parser.htmlParser());
				log.debug("크롤링 정상 수집 : {}", link);
				return Optional.of(doc);
			}
		} catch (Exception e) {
			log.error("({}) 크롤링 중 에러 발생 : {}", link, e.getMessage());
			return Optional.empty();
		}
	}
}
