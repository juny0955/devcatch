package com.davcatch.devcatch.rss;

import java.io.IOException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DaangnRssReader {

	/**
	 * 당근 RSS FEED 파싱
	 * @param feedUrl 당근 테크 블로그 Feed URL
	 * @return Feed
	 */
	public SyndFeed reader(String feedUrl) {
		log.info("당근 RSS FEED 수집 시작");
		try {
			URL url = new URL(feedUrl);
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(url));

			log.info("당근 RSS FEED 정상 수집");
			return feed;
		} catch (FeedException | IOException e) {
			log.info("당근 RSS FEED 수집중 오류 발생 : {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
