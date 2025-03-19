package com.davcatch.devcatch.service.article;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional
	public void save(Article article) {
		try {
			articleRepository.save(article);
		} catch (Exception e) {
			log.info("Article {} 저장중 오류발생", article.getTitle());
		}
	}

	public Optional<Article> getByLastPublishedArticle(Long sourceId) {
		return articleRepository.findLastPublishedArticle(sourceId);
	}

	public List<Article> getSendArticles() {
		return articleRepository.findSendArticles();
	}

	public boolean checkExistsLink(String link) {
		return articleRepository.existsByLink(link);
	}

	public List<Article> getNewArticles() {
		return articleRepository.findNewArticlesTOP6(PageRequest.of(0, 6));
	}

	public Page<Article> getArticlesList(Pageable pageable) {
		return articleRepository.findArticlesList(pageable);
	}
}
