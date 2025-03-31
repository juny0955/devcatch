package com.davcatch.devcatch.common.integration.rss;

import java.io.StringReader;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

	/**
	 * RSS FEED 파싱
	 * @param feedUrl Feed URL
	 * @return Feed
	 */
	public Optional<SyndFeed> reader(String feedUrl) {
		log.debug("RSS FEED 수집 시작 : {}", feedUrl);

		try {
			ResponseEntity<String> response = rssRestTemplate.getForEntity(feedUrl, String.class);
			String xml = response.getBody();

			if (xml != null) {
				SyndFeedInput syndFeedInput = new SyndFeedInput();
				syndFeedInput.setAllowDoctypes(true);
				syndFeedInput.setPreserveWireFeed(true);
				syndFeedInput.setXmlHealerOn(true); // XML 문법 오류 자동 복구 활성화

				SyndFeed feed = syndFeedInput.build(new StringReader(xml));
				log.debug("RSS FEED 정상 수집 URL : {}", feedUrl);
				return Optional.of(feed);
			}

		} catch (FeedException e) {
			log.error("[{}] RSS FEED 수집중 오류 발생 : {}", feedUrl, e.getMessage());
		}

		return Optional.empty();
	}
}
