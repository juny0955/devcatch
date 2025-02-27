package com.davcatch.devcatch.rss;

import java.io.IOException;
import java.net.URL;

import org.springframework.stereotype.Service;

import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.exception.ErrorCode;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RssReader {

	/**
	 * RSS FEED 파싱
	 * @param feedUrl Feed URL
	 * @return Feed
	 */
	public SyndFeed reader(String feedUrl) throws CustomException {
		log.debug("RSS FEED 수집 시작 : {}", feedUrl);
		try {
			URL url = new URL(feedUrl);
			SyndFeedInput syndFeedInput = new SyndFeedInput();
			SyndFeed feed = syndFeedInput.build(new XmlReader(url));

			log.debug("RSS FEED 정상 수집 URL : {}", feedUrl);
			return feed;
		} catch (FeedException | IOException e) {
			log.debug("RSS FEED 수집중 오류 발생 : {}", e.getMessage());
			throw new CustomException(ErrorCode.RSS_PARSE_ERROR);
		}
	}
}
