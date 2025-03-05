package com.davcatch.devcatch.service.schedue.article;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.davcatch.devcatch.domain.Article;
import com.davcatch.devcatch.domain.Source;
import com.davcatch.devcatch.integration.rss.RssReader;
import com.davcatch.devcatch.repository.SourceRepository;
import com.davcatch.devcatch.service.article.ArticleService;
import com.rometools.rome.feed.synd.SyndFeed;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ArticleFromRssScheduler extends ArticleSchedulerService{

	private final RssReader rssReader;
	private final SourceRepository sourceRepository;
	private final ArticleService articleService;

	public ArticleFromRssScheduler(ArticleFactory articleFactory, RssReader rssReader, SourceRepository sourceRepository, ArticleService articleService) {
		super(articleFactory);
		this.rssReader = rssReader;
		this.sourceRepository = sourceRepository;
		this.articleService = articleService;
	}

	@Override
	public void createNewArticles() {
		List<Source> sources = sourceRepository.findAllByIsActiveTrue();

		for (Source source : sources) {
			log.info("[{}] Article 생성 스케줄러 시작", source.getName());

			// rss feed 내용 가져오기
			SyndFeed feed = rssReader.reader(source.getFeedUrl());

			// published_at이 마지막 Article 불러오기
			Optional<Article> lastPublishedArticle = articleService.findByLastPublishedArticle(source.getId());

			List<Article> articles = filterAndCreateArticles(feed, source, lastPublishedArticle);

			if (!articles.isEmpty()) {
				for (Article article : articles)
					articleService.save(article);

				log.info("[{}] Article 저장 : 총 {}개", source.getName(), articles.size());
			} else {
				log.info("[{}] 새로운 Article이 없습니다.", source.getName());
			}
		}
	}
}
