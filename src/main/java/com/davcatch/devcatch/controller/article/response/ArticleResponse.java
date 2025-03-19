package com.davcatch.devcatch.controller.article.response;

import java.util.Date;
import java.util.List;

import com.davcatch.devcatch.controller.main.response.MainArticleResponse;
import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.article.ArticleTag;
import com.davcatch.devcatch.domain.tag.TagType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleResponse {

	private String title;
	private String summary;
	private String link;
	private Date publishedAt;
	private String name;
	private List<TagType> tags;

	public static ArticleResponse of(Article article, List<ArticleTag> articleTags) {
		List<TagType> tagTypes = articleTags.stream()
			.filter(articleTag -> articleTag.getArticle().equals(article))
			.map(articleTag -> articleTag.getTag().getTagType())
			.toList();

		return ArticleResponse.builder()
			.title(article.getTitle())
			.summary(article.getSummary())
			.link(article.getLink())
			.publishedAt(article.getPublishedAt())
			.name(article.getSource().getName())
			.tags(tagTypes)
			.build();
	}
}
