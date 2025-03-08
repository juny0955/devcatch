package com.davcatch.devcatch.integration.rss;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RssReader {

	/**
	 * RSS FEED 파싱
	 * @param feedUrl Feed URL
	 * @return Feed
	 */
	public Optional<SyndFeed> reader(String feedUrl) {
		log.debug("RSS FEED 수집 시작 : {}", feedUrl);
		try {
			URL url = new URL(feedUrl);
			SyndFeedInput syndFeedInput = new SyndFeedInput();
			SyndFeed feed = syndFeedInput.build(new XmlReader(url));

			log.debug("RSS FEED 정상 수집 URL : {}", feedUrl);
			return Optional.of(feed);
		} catch (FeedException | IOException e) {
			log.error("RSS FEED 수집중 오류 발생 : {}", e.getMessage());
			return Optional.empty();
		}
	}
}
