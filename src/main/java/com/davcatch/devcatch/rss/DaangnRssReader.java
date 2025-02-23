package com.davcatch.devcatch.rss;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.davcatch.devcatch.domain.Article;
import com.davcatch.devcatch.domain.Source;
import com.davcatch.devcatch.gpt.GptSummary;
import com.davcatch.devcatch.repository.ArticleRepository;
import com.davcatch.devcatch.repository.SourceRepository;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DaangnRssReader {

	@Value("${daangn.rss}")
	private String feedUrl;

	private final SourceRepository sourceRepository;
	private final ArticleRepository articleRepository;
	private final GptSummary gptSummary;

	public void reader() {
		try {
			URL url = new URL(feedUrl);
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(url));

			Source source = sourceRepository.findByName("daangn")
				.orElseThrow(() -> new IllegalArgumentException("Source를 찾지 못함"));

			// published_at이 마지막 Article 불러오기
			Article lastPublishedArticle = articleRepository.findLastPublishedArticle(source.getId()).orElse(null);

			List<Article> articles = new ArrayList<>();
			// 이전 데이터 없을 경우 전체 저장
			if (lastPublishedArticle == null) {
				for (SyndEntry entry : feed.getEntries()) {
					getSummaryAndAddList(entry, articles, source);
				}
			} else {
				/*
				이전 데이터 있을 경우
				lastPublishedArticle 이후 데이터만 필터링
				GPT 요약 요청 -> Article 생성 후 List에 추가
				 */
				for (SyndEntry entry : feed.getEntries()) {
					if (entry.getPublishedDate().after(lastPublishedArticle.getPublishedAt())) {
						getSummaryAndAddList(entry, articles, source);
					}
				}
			}

			if (!articles.isEmpty())
				articleRepository.saveAll(articles);

		} catch (FeedException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void getSummaryAndAddList(SyndEntry entry, List<Article> articles, Source source) {
		String summary = gptSummary.getSummary(entry.getContents().get(0).getValue());

		articles.add(Article.from(source, entry, summary));
	}
}
