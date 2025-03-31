package com.davcatch.devcatch.common.integration.rss;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RssReaderService {

	/**
	 * RSS FEED 파싱
	 * @param feedUrl Feed URL
	 * @return Feed
	 */
	public Optional<SyndFeed> reader(String feedUrl) {
		log.debug("RSS FEED 수집 시작 : {}", feedUrl);
		HttpURLConnection connection = null;

		try {
			URL url = new URL(feedUrl);
			connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36");
			connection.setRequestProperty("Accept", "application/rss+xml, application/xml, text/xml, */*");

			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);

			try (InputStream inputStream = connection.getInputStream();
				 BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 8192);
				 Reader reader = new InputStreamReader(bufferedInputStream, StandardCharsets.UTF_8);
				 BufferedReader bufferedReader = new BufferedReader(reader, 8192)) {

				SyndFeedInput syndFeedInput = new SyndFeedInput();
				syndFeedInput.setAllowDoctypes(true);
				syndFeedInput.setPreserveWireFeed(true);
				syndFeedInput.setXmlHealerOn(true); // XML 문법 오류 자동 복구 활성화

				SyndFeed feed = syndFeedInput.build(bufferedReader);
				log.debug("RSS FEED 정상 수집 URL : {}", feedUrl);
				return Optional.of(feed);
			}
		} catch (FeedException | IOException e) {
			log.error("[{}] RSS FEED 수집중 오류 발생 : {}", feedUrl, e.getMessage());
			return Optional.empty();
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}
}
