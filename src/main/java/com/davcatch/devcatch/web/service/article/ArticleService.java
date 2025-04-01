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

	public List<Article> getSendArticles() {
		return articleRepository.findSendArticles();
	}

	public List<Article> getNewArticles() {
		return articleRepository.findNewArticlesTOP6(PageRequest.of(0, 6));
	}

	public Page<Article> getArticlesList(Pageable pageable, String keyword, TagType tag) {
		if (keyword != null && keyword.trim().isEmpty())
			keyword = null;

		return articleRepository.findArticlesList(pageable, keyword, tag);
	}

	public Article getArticle(Long articleId) throws CustomException {
		return articleRepository.findArticleDetail(articleId).orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
	}

	public List<Article> getRelatedArticles(Long articleId, List<TagType> tagTypes) {
		return articleRepository.findRelatedArticles(articleId, tagTypes, PageRequest.of(0, 4));
	}
}
