package com.davcatch.devcatch.common.integration.rss;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.davcatch.devcatch.domain.source.Source;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

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

		try {
			byte[] bytes = rssRestClient.get()
				.uri(source.getFeedUrl())
				.retrieve()
				.body(byte[].class);

			if (bytes == null || bytes.length == 0) {
				log.warn("[{}] 피드를 가져오지 못했습니다", source.getName());
				return Optional.empty();
			}

			try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				 XmlReader xmlReader = new XmlReader(bais)) {

				SyndFeedInput input = new SyndFeedInput();
				input.setPreserveWireFeed(true);
				input.setAllowDoctypes(true);
				input.setXmlHealerOn(true); // XML 문법 오류 자동 복구
				SyndFeed feed = input.build(xmlReader);
				return Optional.of(feed);
			}
		} catch (FeedException | IOException e) {
			log.error("[{}] RSS FEED 수집중 오류 발생 : {}", source.getName(), e.getMessage());
			return Optional.empty();
		}
	}
}
