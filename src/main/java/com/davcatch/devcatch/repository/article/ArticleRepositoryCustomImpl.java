package com.davcatch.devcatch.repository.article;

import static com.davcatch.devcatch.domain.article.QArticle.*;
import static com.davcatch.devcatch.domain.article.QArticleTag.*;
import static com.davcatch.devcatch.domain.source.QSource.*;
import static com.davcatch.devcatch.domain.tag.QTag.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.tag.TagType;
import com.davcatch.devcatch.web.controller.article.response.ArticleResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryCustomImpl implements ArticleRepositoryCustom{

	private static final long MAIN_PAGE_ARTICLE_SIZE = 6L;
	private static final long RELATED_ARTICLE_SIZE = 4L;

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<Article> findLastPublishedArticle(Long sourceId) {
		Article result = queryFactory
			.selectFrom(article)
			.where(article.source.id.eq(sourceId))
			.orderBy(article.publishedAt.desc())
			.limit(1)
			.fetchOne();

		return Optional.ofNullable(result);
	}

	@Override
	public List<ArticleResponse> findNewArticlesTOP6() {
		List<Long> topIds = queryFactory
			.select(article.id)
			.from(article)
			.orderBy(article.publishedAt.desc())
			.limit(MAIN_PAGE_ARTICLE_SIZE)
			.fetch();

		Map<Long, ArticleResponse> transform = queryFactory
			.from(article)
			.join(article.source, source)
			.leftJoin(article.articleTags, articleTag)
			.leftJoin(articleTag.tag, tag)
			.where(article.id.in(topIds.toArray(new Long[0])))
			.transform(
				GroupBy.groupBy(article.id).as(
					createArticleResponseProjection()
				)
			);

		return resortTransform(transform);
	}

	@Override
	public List<Article> findSendArticles() {
		return queryFactory
			.selectFrom(article)
			.join(article.source, source).fetchJoin()
			.leftJoin(article.articleTags, articleTag).fetchJoin()
			.leftJoin(articleTag.tag, tag).fetchJoin()
			.where(article.isSent.eq(false))
			.fetch();
	}

	@Override
	public Page<ArticleResponse> findArticlesList(Pageable pageable, String keyword, TagType tagType) {
		BooleanBuilder builder = new BooleanBuilder();

		JPQLQuery<Long> countQuery = queryFactory
			.select(article.id.countDistinct())
			.from(article);

		if (keyword != null && !keyword.isEmpty()) {
			builder.and(article.title.lower().like(keyword.toLowerCase() + "%"));

			countQuery
				.where(article.title.lower().like(keyword.toLowerCase() + "%"));
		}

		if (tagType != null) {
			builder.and(tag.tagType.eq(tagType));

			countQuery
				.leftJoin(article.articleTags, articleTag)
				.leftJoin(articleTag.tag, tag)
				.where(tag.tagType.eq(tagType));
		}

		Long count = countQuery.fetchOne();
		long total = count != null ? count : 0;

		Map<Long, ArticleResponse> transform = queryFactory
			.from(article)
			.join(article.source, source)
			.leftJoin(article.articleTags, articleTag)
			.leftJoin(articleTag.tag, tag)
			.where(builder)
			.orderBy(article.publishedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.transform(
				GroupBy.groupBy(article.id).as(
					createArticleResponseProjection()
				)
			);

		return new PageImpl<>(resortTransform(transform), pageable, total);
	}

	@Override
	public List<Article> findDashboardList(Pageable pageable) {
		return queryFactory
			.selectFrom(article)
			.orderBy(article.publishedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	@Override
	public Optional<ArticleResponse> findArticleDetail(Long articleId) {
		Map<Long, ArticleResponse> transform = queryFactory
			.from(article)
			.join(article.source, source)
			.leftJoin(article.articleTags, articleTag)
			.leftJoin(articleTag.tag, tag)
			.where(article.id.eq(articleId))
			.transform(
				GroupBy.groupBy(article.id).as(
					createArticleResponseProjection()
				)
			);

		return Optional.ofNullable(transform.get(articleId));
	}

	@Override
	public List<ArticleResponse> findRelatedArticles(Long articleId, List<TagType> tagTypes) {
		if (tagTypes == null || tagTypes.isEmpty())
			return Collections.emptyList();

		Map<Long, ArticleResponse> transform = queryFactory
			.from(article)
			.join(article.source, source)
			.join(article.articleTags, articleTag)
			.join(articleTag.tag, tag)
			.where(
				article.id.ne(articleId),
				tag.tagType.in(tagTypes)
			)
			.orderBy(article.publishedAt.desc())
			.limit(RELATED_ARTICLE_SIZE)
			.transform(
				GroupBy.groupBy(article.id).as(
					createArticleResponseProjection()
				)
			);

		return new ArrayList<>(transform.values());
	}

	/**
	 * transfrom 결과가 순서를 보장하지 않아
	 * publishedAt을 기준으로 다시 정렬
	 * @param transform 쿼리 결과
	 * @return 정렬된 Response
	 */
	private List<ArticleResponse> resortTransform(Map<Long, ArticleResponse> transform) {
		return transform.values().stream()
			.sorted(Comparator.comparing(ArticleResponse::publishedAt).reversed())
			.toList();
	}

	/**
	 * ArticleResponse Projection 생성
	 */
	private ConstructorExpression<ArticleResponse> createArticleResponseProjection() {
		return Projections.constructor(
			ArticleResponse.class,
			article.id,
			article.title,
			article.summary,
			article.link,
			article.publishedAt,
			source.name,
			Projections.list(tag.tagType)
		);
	}
}
