package com.davcatch.devcatch.repository.article;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.tag.Tag;
import com.davcatch.devcatch.domain.tag.TagType;

public interface ArticleRepository extends JpaRepository<Article, Long> {

	@Query(value = "select * from article a "
		+ "where source_id = :sourceId "
		+ "order by published_at desc "
		+ "limit 1 ", nativeQuery = true)
	Optional<Article> findLastPublishedArticle(Long sourceId);

	@EntityGraph(attributePaths = {"articleTags", "articleTags.tag"})
	@Query("select a from Article a "
		+ "where a.isSent = false ")
	List<Article> findSendArticles();

	@EntityGraph(attributePaths = {"source"})
	@Query("select a from Article a "
		+ "order by a.publishedAt desc")
	List<Article> findNewArticlesTOP6(Pageable pageable);

	@EntityGraph(attributePaths = {"source"})
	@Query("select distinct a from Article a "
		+ "left join a.articleTags at "
		+ "left join at.tag t "
		+ "where (:keyword is null or lower(a.title) like lower(concat(:keyword, '%'))) "
		+ "and (:tag is null or t.tagType = :tag) "
		+ "order by a.publishedAt desc")
	Page<Article> findArticlesList(Pageable pageable, String keyword, TagType tag);

	@Query("select a from Article a "
		+ "order by a.publishedAt desc")
	List<Article> findDashboardList(Pageable pageable);

	@Query("select a.link from Article a where a.link in :links")
	Set<String> findExistsLinks(Set<String> links);

	@Query("select count(a.id) from Article a ")
	int findTotalArticleSize();

	@EntityGraph(attributePaths = {"source"})
	@Query("select a from Article a where a.id = :articleId")
	Optional<Article> findArticleDetail(Long articleId);

	@EntityGraph(attributePaths = {"source"})
	@Query("select distinct a from Article a "
		+ "join a.articleTags at "
		+ "join at.tag t "
		+ "where a.id != :articleId "
		+ "and t.tagType in :tagTypes "
		+ "order by a.publishedAt desc ")
	List<Article> findRelatedArticles(Long articleId, List<TagType> tagTypes, Pageable pageable);
}
