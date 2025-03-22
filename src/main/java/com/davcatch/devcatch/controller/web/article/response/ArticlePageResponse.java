package com.davcatch.devcatch.controller.web.article.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticlePageResponse {
	private List<ArticleResponse> contents;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
