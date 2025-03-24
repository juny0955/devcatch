package com.davcatch.devcatch.scheduler.article.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContentResponse {

	private String summary;
	private List<String> tag;
}
