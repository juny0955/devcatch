package com.davcatch.devcatch.integration.gpt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.exception.ErrorCode;
import com.davcatch.devcatch.integration.gpt.request.GptRequest;
import com.davcatch.devcatch.integration.gpt.response.GptResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GptSummaryService {

	private static final String URL = "https://api.openai.com/v1/chat/completions";

	private final RestTemplate restTemplate;

	@Value("${gpt.model}")
	private String model;

	@Value("${gpt.sys.prompt}")
	private String sysPrompt;

	/**
	 * 본문 내용을 사용해 GPT 요약 요청
	 * @param content 본문 내용
	 * @return GPT 요약 내용
	 */
	public GptResponse getSummary(String content) throws CustomException {
		log.info("GPT API 요청 시작");

		GptResponse response;

		try {
			GptRequest request = GptRequest.create(content, model, sysPrompt);
			response = restTemplate.postForObject(URL, request, GptResponse.class);
		} catch (Exception e) {
			log.info("GPT API 요청중 에러 발생 : {}", e.getMessage());
			throw new CustomException(ErrorCode.GPT_REQUEST_ERROR);
		}

		if (response == null) {
			log.info("GPT API 응답 BODY 없음");
			throw new CustomException(ErrorCode.GPT_REQUEST_BODY_NULL);
		}

		log.info("GPT API 요청 정상 종료");
		return response;
	}
}
