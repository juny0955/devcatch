package com.davcatch.devcatch.service.schedue.article;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.davcatch.devcatch.domain.Article;
import com.davcatch.devcatch.domain.Source;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ArticleSchedulerService {

	private final ArticleFactory articleFactory;

	/** 새로운 Article 생성
	 * RSS 파싱 -> GPT 요약 -> DB 저장
	 */
	abstract public void createNewArticles();

	/** Article 필터
	 * 해당 Source에 해당하는 데이터가 없을 시 어제 날짜 기준 이후 데이터 변환
	 * 해당 Source에 해당하는 데이터가 있을 시 마지막 게시글 이후의 것들만 변환
	 *
	 * @param feed 필터링할 feed
	 * @param source 해당 Source
	 * @param lastPublishedArticle 해당 Source의 마지막 등록된 Article
	 */
	protected List<Article> filterAndCreateArticles(SyndFeed feed, Source source, Optional<Article> lastPublishedArticle) {
		List<Article> articles = new ArrayList<>();
		Date date;

		if (lastPublishedArticle.isEmpty()) {
			LocalDate yesterDay = LocalDate.now().minusDays(1);
			date = Date.from(yesterDay.atStartOfDay(ZoneId.systemDefault()).toInstant());
		} else
			date = lastPublishedArticle.get().getPublishedAt();


		for (SyndEntry entry : feed.getEntries()) {
			// RSS Feed에 PublishedDate가 없는 경우 UpdateDate로 사용
			Date entryDate = entry.getPublishedDate() != null ? entry.getPublishedDate() : entry.getUpdatedDate();

			if (entryDate != null && entryDate.after(date))
				articles.add(articleFactory.createArticle(source, entry));
		}

		return articles;
	}
}
