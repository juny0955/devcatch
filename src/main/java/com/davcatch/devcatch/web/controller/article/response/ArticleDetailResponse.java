package com.davcatch.devcatch.web.controller.article.response;

import java.util.Date;
import java.util.List;

import com.davcatch.devcatch.domain.tag.TagType;

import lombok.Builder;

@Builder
public record ArticleDetailResponse(
	Long id,
	String title,
	String summary,
	String link,
	Date publishedAt,
	String sourceName,
	List<TagType> tags
) {
}
