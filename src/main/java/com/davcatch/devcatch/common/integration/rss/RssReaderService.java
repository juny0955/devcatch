package com.davcatch.devcatch.common.integration.rss;

import java.io.StringReader;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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

	private final RestClient rssRestClient;

	/**
	 * RSS FEED 파싱
	 * @param source 해당 소스
	 * @return Feed
	 */
	public Optional<SyndFeed> reader(Source source) {
		log.debug("[{}] RSS FEED 수집 시작", source.getName());

		SyndFeed feed = null;
		try {
			String rssFeedXml = rssRestClient.get()
				.uri(source.getFeedUrl())
				.retrieve()
				.body(String.class);

			if (rssFeedXml == null || rssFeedXml.isBlank()) {
				log.warn("[{}] RSS FEED를 가져올 수 없습니다", source.getName());
				return Optional.empty();
			}

			SyndFeedInput syndFeedInput = new SyndFeedInput();
			syndFeedInput.setAllowDoctypes(true);
			syndFeedInput.setPreserveWireFeed(true);
			syndFeedInput.setXmlHealerOn(true); // XML 문법 오류 자동 복구 활성화

			feed = syndFeedInput.build(new StringReader(rssFeedXml));
			log.debug("RSS FEED 정상 수집 : {}", source.getName());
		} catch (FeedException e) {
			log.error("[{}] RSS FEED 수집중 오류 발생 : {}", source.getName(), e.getMessage());
		}

		return Optional.ofNullable(feed);
	}
}
