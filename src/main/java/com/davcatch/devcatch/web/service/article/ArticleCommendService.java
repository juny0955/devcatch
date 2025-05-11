package com.davcatch.devcatch.web.service.article;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;
import com.davcatch.devcatch.domain.tag.TagType;
import com.davcatch.devcatch.repository.article.ArticleRepository;
import com.davcatch.devcatch.web.controller.article.response.ArticleDetailResponse;
import com.davcatch.devcatch.web.controller.article.response.ArticleResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ArticleCommendService {

	private final ArticleRepository articleRepository;

	/**
	 * 새로운 아티클 조회 (메인 페이지용)
	 * 최신 6개 항목
	 * @return 아티클 리스트
	 */
	public List<ArticleResponse> getNewArticles() {
		return articleRepository.findNewArticlesTOP6();
	}

	/**
	 * 아티클 목록 조회
	 * @param pageable 페이지
	 * @param keyword 검색 키워드
	 * @param tag 검색 태그
	 * @return 아티클 리스트
	 */
	public Page<ArticleResponse> getArticlesList(Pageable pageable, String keyword, TagType tag) {
		if (keyword != null && keyword.trim().isEmpty())
			keyword = null;

		return articleRepository.findArticlesList(pageable, keyword, tag);
	}

	/**
	 * 아티클 상세 조회
	 * @param articleId 조회할 아티클 ID
	 * @return 조회된 아티클
	 * @throws CustomException
	 */
	public ArticleResponse getArticleDetail(Long articleId) throws CustomException {
		return articleRepository.findArticleDetail(articleId).orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
	}

	/**
	 * 연관 아티클 조회
	 * @param articleId 조회할 ID
	 * @param tagTypes 아티클 태그
	 * @return 아티클 리스트
	 */
	public List<ArticleResponse> getRelatedArticles(Long articleId, List<TagType> tagTypes) {
		List<TagType> filteredNullTagTypes = tagTypes == null ?
			Collections.emptyList() :
			tagTypes.stream()
				.filter(Objects::nonNull)
				.toList();

		if (filteredNullTagTypes.isEmpty())
			return Collections.emptyList();

		return articleRepository.findRelatedArticles(articleId, filteredNullTagTypes);
	}

}
