package com.davcatch.devcatch.web.service.article;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;
import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.tag.TagType;
import com.davcatch.devcatch.repository.article.ArticleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ArticleService {

	private final ArticleRepository articleRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void save(Article article) throws CustomException {
		try {
			articleRepository.save(article);
		} catch (Exception e) {
			log.info("Article {} 저장중 오류발생", article.getTitle());
			throw new CustomException(ErrorCode.SERVER_ERROR);
		}
	}

	/**
	 * 사용자 알림 아티클 조회
	 * @return 아티클 리스트
	 */
	public List<Article> getSendArticles() {
		return articleRepository.findSendArticles();
	}

	/**
	 * 새로운 아티클 조회 (메인 페이지용)
	 * 최신 6개 항목
	 * @return 아티클 리스트
	 */
	public List<Article> getNewArticles() {
		return articleRepository.findNewArticlesTOP6(PageRequest.of(0, 6));
	}

	/**
	 * 아티클 목록 조회
	 * @param pageable 페이지
	 * @param keyword 검색 키워드
	 * @param tag 검색 태그
	 * @return 아티클 리스트
	 */
	public Page<Article> getArticlesList(Pageable pageable, String keyword, TagType tag) {
		if (keyword != null && keyword.trim().isEmpty())
			keyword = null;

		return articleRepository.findArticlesList(pageable, keyword, tag);
	}

	/**
	 * ID로 조회
	 * @param articleId 조회할 ID
	 * @return 조회된 아티클
	 * @throws CustomException
	 */
	public Article getArticle(Long articleId) throws CustomException {
		return articleRepository.findArticleDetail(articleId).orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
	}

	/**
	 * 연관 아티클 조회
	 * @param articleId 조회할 ID
	 * @param tagTypes 아티클 태그
	 * @return 아티클 리스트
	 */
	public List<Article> getRelatedArticles(Long articleId, List<TagType> tagTypes) {
		return articleRepository.findRelatedArticles(articleId, tagTypes, PageRequest.of(0, 4));
	}

	/**
	 * 해당 소스의 마지막 발행된 아티클 조회
	 * @param sourceId 해당 소스 ID
	 * @return 조회된 아티클
	 * @throws CustomException
	 */
	public Article getLastPublishedArticle(Long sourceId) throws CustomException {
		return articleRepository.findLastPublishedArticle(sourceId).orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
	}
}
