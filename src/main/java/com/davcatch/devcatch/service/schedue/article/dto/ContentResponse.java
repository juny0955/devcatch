package com.davcatch.devcatch.service.schedue.article.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContentResponse {

	private String summary;
	private List<String> tag;
}
