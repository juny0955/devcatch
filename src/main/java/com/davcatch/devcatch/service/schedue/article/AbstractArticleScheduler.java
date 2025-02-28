package com.davcatch.devcatch.service.schedue.article;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.davcatch.devcatch.domain.Article;
import com.davcatch.devcatch.domain.Source;
import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.exception.ErrorCode;
import com.davcatch.devcatch.integration.rss.RssReader;
import com.davcatch.devcatch.repository.ArticleRepository;
import com.davcatch.devcatch.repository.SourceRepository;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Transactional
@Slf4j
public abstract class AbstractArticleScheduler implements ArticleSchedulerService{

	protected final RssReader rssReader;
	protected final SourceRepository sourceRepository;
	protected final ArticleRepository articleRepository;
	protected final ArticleFactory articleFactory;

	@Override
	public void createNewArticles() throws CustomException {
		log.info("{} Article 생성 스케줄러 시작", getSourceName());

		Source source = sourceRepository.findByName("daangn")
			.orElseThrow(() -> new CustomException(ErrorCode.SOURCE_NOT_FOUND));

		// rss feed 내용 가져오기
		SyndFeed feed = rssReader.reader(source.getFeedUrl());

		// published_at이 마지막 Article 불러오기
		Optional<Article> lastPublishedArticle = articleRepository.findLastPublishedArticle(source.getId());

		List<Article> articles = filterAndCreateArticles(feed, source, lastPublishedArticle);

		if (!articles.isEmpty()) {
			articleRepository.saveAll(articles);
			log.info("[{}] Article 저장 : 총 {}개", getSourceName(), articles.size());
		} else {
			log.info("[{}] 새로운 Article이 없습니다.", getSourceName());
		}
	}

	/**
	 * RSS 피드의 각 엔트리를 Article로 변환하고, 마지막 게시글 이후의 것들만 반환
	 */
	private List<Article> filterAndCreateArticles(SyndFeed feed, Source source, Optional<Article> lastPublishedArticle) throws CustomException {

		List<Article> articles = new ArrayList<>();
		for (SyndEntry entry : feed.getEntries()) {
			// 이전 데이터가 없거나, 마지막 게시글 이후의 게시글만 처리
			if (lastPublishedArticle.isEmpty() || entry.getPublishedDate().after(lastPublishedArticle.get().getPublishedAt())) {
				articles.add(articleFactory.createArticle(source, entry));
			}
		}
		return articles;
	}

	protected abstract String getSourceName();
}
