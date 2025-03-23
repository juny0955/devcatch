package com.davcatch.devcatch.schedue.article.dto;

import java.util.ArrayList;
import java.util.List;

import com.davcatch.devcatch.domain.tag.TagType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ArticleSummary {

	private String summary;

	@Builder.Default
	private List<TagType> tags = new ArrayList<>();

	public static ArticleSummary of(String summary, List<TagType> tags) {
		return ArticleSummary.builder()
			.summary(summary)
			.tags(tags)
			.build();
	}
}
