package com.davcatch.devcatch.service.schedue.article;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.davcatch.devcatch.domain.Article;
import com.davcatch.devcatch.domain.Source;
import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.integration.rss.RssReader;
import com.davcatch.devcatch.repository.ArticleRepository;
import com.davcatch.devcatch.repository.SourceRepository;
import com.rometools.rome.feed.synd.SyndFeed;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ArticleFromRssScheduler extends ArticleSchedulerService{

	private final RssReader rssReader;
	private final SourceRepository sourceRepository;
	private final ArticleRepository articleRepository;

	public ArticleFromRssScheduler(ArticleFactory articleFactory, RssReader rssReader, SourceRepository sourceRepository, ArticleRepository articleRepository) {
		super(articleFactory);
		this.rssReader = rssReader;
		this.sourceRepository = sourceRepository;
		this.articleRepository = articleRepository;
	}

	@Override
	public void createNewArticles() throws CustomException {
		List<Source> sources = sourceRepository.findAllByIsActiveTrue();

		for (Source source : sources) {
			log.info("{} Article 생성 스케줄러 시작", source.getName());

			// rss feed 내용 가져오기
			SyndFeed feed = rssReader.reader(source.getFeedUrl());

			// published_at이 마지막 Article 불러오기
			Optional<Article> lastPublishedArticle = articleRepository.findLastPublishedArticle(source.getId());

			List<Article> articles = filterAndCreateArticles(feed, source, lastPublishedArticle);

			if (!articles.isEmpty()) {
				articleRepository.saveAll(articles);
				log.info("[{}] Article 저장 : 총 {}개", source.getName(), articles.size());
			} else {
				log.info("[{}] 새로운 Article이 없습니다.", source.getName());
			}
		}
	}
}
