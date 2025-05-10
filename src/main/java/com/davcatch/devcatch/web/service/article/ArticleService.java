package com.davcatch.devcatch.web.service.article;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;
import com.davcatch.devcatch.domain.article.Article;
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
	 * 해당 소스의 마지막 발행된 아티클 조회
	 * @param sourceId 해당 소스 ID
	 * @return 조회된 아티클
	 * @throws CustomException
	 */
	public Article getLastPublishedArticle(Long sourceId) throws CustomException {
		return articleRepository.findLastPublishedArticle(sourceId).orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
	}

	public Set<String> getArticlesByLink(Set<String> links) {
		return articleRepository.findExistsLinks(links);
	}
}
