package com.davcatch.devcatch.common.scheduler.article.dto;

import java.util.List;
import java.util.Set;

import com.davcatch.devcatch.domain.tag.TagType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Content {

	private String summary;
	private List<TagType> tag;

	public static Content of(String summary, Set<TagType> tagTypes) {
		return Content.builder()
			.summary(summary)
			.tag(tagTypes.stream().toList())
			.build();
	}
}
