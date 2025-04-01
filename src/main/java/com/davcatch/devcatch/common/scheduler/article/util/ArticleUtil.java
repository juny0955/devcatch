package com.davcatch.devcatch.common.scheduler.article.util;

import java.util.List;

import com.davcatch.devcatch.common.scheduler.article.dto.ArticleSummary;
import com.davcatch.devcatch.common.scheduler.article.dto.ParsedArticle;
import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.article.ArticleTag;
import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.domain.tag.Tag;

public final class ArticleUtil {

	public static Article createNewArticle(Source source, ParsedArticle parsedArticle, ArticleSummary summary, List<Tag> tags) {
		Article article = Article.of(source, parsedArticle, summary.getSummary());
		tags.forEach(tag -> article.addArticleTag(ArticleTag.of(article, tag)));

		return article;
	}
}
