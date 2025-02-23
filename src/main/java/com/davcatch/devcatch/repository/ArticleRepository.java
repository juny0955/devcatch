package com.davcatch.devcatch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.davcatch.devcatch.domain.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {

	@Query(value = "select a from Article a "
		+ "where a.source = :sourceId "
		+ "order by a.publishedAt desc "
		+ "limit 1 ", nativeQuery = true)
	Optional<Article> findLastPublishedArticle(Long sourceId);
}
