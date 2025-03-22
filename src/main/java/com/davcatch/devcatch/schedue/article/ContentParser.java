package com.davcatch.devcatch.schedue.article;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.davcatch.devcatch.domain.tag.TagType;
import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;
import com.davcatch.devcatch.integration.gpt.response.GptResponse;
import com.davcatch.devcatch.schedue.article.dto.ContentResponse;
import com.davcatch.devcatch.schedue.article.dto.Content;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContentParser {

	private final ObjectMapper objectMapper;

	public Content parseContent(GptResponse response) throws CustomException {
		try {
			ContentResponse contentResponse = objectMapper.readValue(response.getChoices().get(0).getMessage().getContent(), ContentResponse.class);

			Set<TagType> tagTypes = contentResponse.getTag().stream()
				.map(this::mapTag)
				.collect(Collectors.toSet());

			return Content.of(contentResponse.getSummary(), tagTypes);
		} catch (JsonProcessingException e) {
			log.info("GPT Rsponse 파싱중 오류 발생 : {}", e.getMessage());
			throw new CustomException(ErrorCode.CONTENT_PARSE_ERROR);
		}
	}

	private TagType mapTag(String tag) {
		return switch (tag.toLowerCase()) {
			case "backend" -> TagType.BACKEND;
			case "frontend" -> TagType.FRONTEND;
			case "ai" -> TagType.AI;
			case "cloud" -> TagType.CLOUD;
			case "devops" -> TagType.DEVOPS;
			case "data" -> TagType.DATA;
			case "mobile" -> TagType.MOBILE;
			case "security" -> TagType.SECURITY;
			default -> TagType.ETC;
		};
	}
}
