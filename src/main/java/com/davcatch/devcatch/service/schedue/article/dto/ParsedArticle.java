package com.davcatch.devcatch.service.schedue.article.dto;

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

	public static ParsedArticle of(String content, SyndEntry entry, boolean useLink) {
		String link = useLink ? entry.getLink() : entry.getUri();

		return ParsedArticle.builder()
			.title(entry.getTitle())
			.link(link)
			.content(content)
			.publishedAt(entry.getPublishedDate() != null ? entry.getPublishedDate() : entry.getUpdatedDate())
			.build();
	}
}
