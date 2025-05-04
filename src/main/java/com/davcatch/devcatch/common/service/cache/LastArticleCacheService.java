package com.davcatch.devcatch.common.service.cache;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.davcatch.devcatch.common.config.CacheConfig;
import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.web.service.article.ArticleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LastArticleCacheService {

	private final ArticleService articleService;

	/**
	 * SourceId로 해당 Source 아티클 마지막 발행일자 조회
	 * 캐시에 Miss -> DB 조회 후 캐시 저장
	 * @param sourceId key
	 * @return 발행일자
	 */
	@Cacheable(
		value = CacheConfig.LAST_ARTICLE_CACHE,
		key = "#sourceId",
		unless = "#result == null"
	)
	public Date getLastPublishedDate(Long sourceId) {
		log.debug("캐시 MISS: 소스 ID {} DB 조회 시작", sourceId);
		return getLastPublishedDateFromDB(sourceId);
	}

	/**
	 * 발행일자 캐시 업데이트
	 * @param sourceId key
	 * @param publishedAt value
	 */
	@CachePut(
		value = CacheConfig.LAST_ARTICLE_CACHE,
		key = "#sourceId"
	)
	public Date updateLastPublishedDate(Long sourceId, Date publishedAt) {
		log.debug("캐시 업데이트: 소스 ID {} 발행일자 {}", sourceId, publishedAt);
		return publishedAt;
	}

	/**
	 * DB에서 조회
	 * 예외 발생시 어제 날짜 리턴
	 * @param sourceId 소스 ID
	 * @return 발행일자
	 */
	private Date getLastPublishedDateFromDB(Long sourceId) {
		try {
			Article lastPublishedArticle = articleService.getLastPublishedArticle(sourceId);
			return lastPublishedArticle.getPublishedAt();
		} catch (Exception e) {
			return Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
	}

}
