package com.davcatch.devcatch.schedue.article.dto;

import java.util.Date;

import com.rometools.rome.feed.synd.SyndEntry;

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
