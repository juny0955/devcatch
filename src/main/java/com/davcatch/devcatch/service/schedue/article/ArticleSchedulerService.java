package com.davcatch.devcatch.service.schedue.article;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.davcatch.devcatch.domain.Article;
import com.davcatch.devcatch.domain.Source;
import com.davcatch.devcatch.exception.CustomException;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ArticleSchedulerService {

	private final ArticleFactory articleFactory;

	/** 새로운 Article 생성
	 * RSS 파싱 -> GPT 요약 -> DB 저장
	 * @throws CustomException
	 */
	abstract public void createNewArticles() throws CustomException;

	/**
	 * RSS 피드의 각 엔트리를 Article로 변환하고, 마지막 게시글 이후의 것들만 반환
	 */
	protected List<Article> filterAndCreateArticles(SyndFeed feed, Source source, Optional<Article> lastPublishedArticle) {

		List<Article> articles = new ArrayList<>();
		for (SyndEntry entry : feed.getEntries()) {
			// 이전 데이터가 없거나, 마지막 게시글 이후의 게시글만 처리
			if (lastPublishedArticle.isEmpty() || entry.getPublishedDate().after(lastPublishedArticle.get().getPublishedAt())) {
				articles.add(articleFactory.createArticle(source, entry));
			}
		}
		return articles;
	}
}
