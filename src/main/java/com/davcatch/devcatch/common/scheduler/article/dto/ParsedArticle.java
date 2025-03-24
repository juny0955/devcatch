package com.davcatch.devcatch.common.scheduler.article.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ParsedArticle {
	private String title;
	private String link;
	private String content;
	private Date publishedAt;
}
