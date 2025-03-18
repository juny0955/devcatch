package com.davcatch.devcatch.repository.article;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.article.ArticleTag;

public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {

	@EntityGraph(attributePaths = {"tag"})
	@Query("select at from ArticleTag at "
		+ "where at.article in :articles")
	List<ArticleTag> findByArticles(List<Article> articles);
}
