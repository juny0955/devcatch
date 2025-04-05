package com.davcatch.devcatch.common.integration.rss;

import java.io.StringReader;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.davcatch.devcatch.domain.source.Source;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RssReaderService {

	private final RestTemplate rssRestTemplate;
	private final RestTemplate cloudflareRssRestTemplate;

	/**
	 * RSS FEED 파싱
	 * @param feedUrl Feed URL
	 * @return Feed
	 */
	public Optional<SyndFeed> reader(Source source) {
		log.debug("RSS FEED 수집 시작 : {}", source.getFeedUrl());

		try {
			ResponseEntity<String> response;
			if (source.getName().equals("우아한형제들")) {
				cloudflareRssRestTemplate.getForEntity(source.getMainUrl(), String.class);
				response = cloudflareRssRestTemplate.getForEntity(source.getFeedUrl(), String.class);
			} else
				response = rssRestTemplate.getForEntity(source.getFeedUrl(), String.class);

			String xml = response.getBody();

			if (xml != null) {
				SyndFeedInput syndFeedInput = new SyndFeedInput();
				syndFeedInput.setAllowDoctypes(true);
				syndFeedInput.setPreserveWireFeed(true);
				syndFeedInput.setXmlHealerOn(true); // XML 문법 오류 자동 복구 활성화

				SyndFeed feed = syndFeedInput.build(new StringReader(xml));
				log.debug("RSS FEED 정상 수집 : {}", source.getName());
				return Optional.of(feed);
			}

		} catch (FeedException e) {
			log.error("[{}] RSS FEED 수집중 오류 발생 : {}", source.getName(), e.getMessage());
		}

		return Optional.empty();
	}
}
