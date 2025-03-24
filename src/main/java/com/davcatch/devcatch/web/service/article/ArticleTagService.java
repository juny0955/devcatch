package com.davcatch.devcatch.service.article;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.article.ArticleTag;
import com.davcatch.devcatch.repository.article.ArticleTagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleTagService {

	private final ArticleTagRepository articleTagRepository;

	public List<ArticleTag> getArticleTagByArticlesWithTag(List<Article> articles) {
		return articleTagRepository.findByArticles(articles);
	}
}
