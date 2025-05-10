package com.davcatch.devcatch.repository.article;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.davcatch.devcatch.domain.article.Article;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustom {

	@Query("select a.link from Article a where a.link in :links")
	Set<String> findExistsLinks(Set<String> links);

	@Query("select count(a.id) from Article a ")
	int findTotalArticleSize();
}
