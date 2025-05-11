package com.davcatch.devcatch.repository.article;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.tag.TagType;
import com.davcatch.devcatch.web.controller.article.response.ArticleDetailResponse;
import com.davcatch.devcatch.web.controller.article.response.ArticleResponse;

public interface ArticleRepositoryCustom {

	/**
	 * 마지막 발행된 아티클 조회
	 * @param sourceId 해당 소스 ID
	 * @return 마지막 발행된 아티클
	 */
	Optional<Article> findLastPublishedArticle(Long sourceId);

	/**
	 * 사용자에게 알림 보낼 아티클 조회
	 * @return 아티클 리스트
	 */
	List<Article> findSendArticles();

	/**
	 * 메인 페이지용 최신 아티클 조회
	 * @return 아티클 리스트
	 */
	List<ArticleResponse> findNewArticlesTOP6();

	/**
	 * 아티클 검색
	 * @param pageable 페이징 기본 10개
	 * @param keyword 키워드
	 * @param tag 태그
	 * @return 아티클 리스트
	 */
	Page<ArticleResponse> findArticlesList(Pageable pageable, String keyword, TagType tag);

	/**
	 * 어드민 대시보드용 아티클 조회
	 * @param pageable 페이징 기본 5개
	 * @return 아티클 목록
	 */
	List<Article> findDashboardList(Pageable pageable);


	/**
	 * 아티클 상세 조회
	 *
	 * @param articleId 아티클 ID
	 * @return 아티클 상세
	 */
	Optional<ArticleResponse> findArticleDetail(Long articleId);

	/**
	 * 연관된 아티클 조회
	 * @param articleId 해당 아티클 ID
	 * @param tagTypes 해당 아티클 TagType 리스트
	 * @return 연관 아티클 목록
	 */
	List<ArticleResponse> findRelatedArticles(Long articleId, List<TagType> tagTypes);
}
