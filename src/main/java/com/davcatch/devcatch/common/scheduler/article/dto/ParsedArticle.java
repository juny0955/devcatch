package com.davcatch.devcatch.common.scheduler.article.dto;

import java.util.Date;

import com.davcatch.devcatch.domain.source.Source;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

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

	public static ParsedArticle of(SyndEntry syndEntry, Source source, String content) {
		return ParsedArticle.builder()
			.title(syndEntry.getTitle())
			.link(source.isUseLink() ? syndEntry.getLink() : syndEntry.getUri())
			.content(content)
			.publishedAt(syndEntry.getPublishedDate() != null ? syndEntry.getPublishedDate() : syndEntry.getUpdatedDate())
			.build();
	}
}
