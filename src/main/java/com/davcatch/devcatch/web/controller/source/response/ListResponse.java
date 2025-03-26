package com.davcatch.devcatch.web.controller.source.response;

import com.davcatch.devcatch.domain.source.Source;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ListResponse {

	private String name;
	private String mainUrl;

	public static ListResponse from(Source source) {
		return ListResponse.builder()
			.name(source.getName())
			.mainUrl(source.getMainUrl())
			.build();
	}
}
