package com.davcatch.devcatch.integration.crawling;

import java.io.IOException;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebCrawler {

	/**
	 * 크롤링
	 * @param link site link
	 * @return Document
	 */
	public Optional<Document> getDocument(String link) {
		log.debug("크롤링 시작 : {}", link);
		try {
			Document document = Jsoup.connect(link).get();
			log.debug("크롤링 정상 수집 : {}", link);
			
			return Optional.of(document);
		} catch (IOException e) {
			log.error("크롤링 중 에러 발생 : {}", e.getMessage());
			return Optional.empty();
		}
	}
}
