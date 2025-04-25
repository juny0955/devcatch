package com.davcatch.devcatch.common.integration.gpt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;
import com.davcatch.devcatch.common.integration.gpt.request.GptRequest;
import com.davcatch.devcatch.common.integration.gpt.response.GptResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GptSummaryService {

	private static final String GPT_API_URL = "https://api.openai.com/v1/chat/completions";

	private final RestClient gptApiRestClient;

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
		log.debug("GPT API 요청 시작");

		GptResponse response = null;

		try {
			GptRequest request = GptRequest.create(content, model, sysPrompt);
			response = gptApiRestClient.post()
				.uri(GPT_API_URL)
				.body(request)
				.retrieve()
				.body(GptResponse.class);

		} catch (Exception e) {
			log.error("GPT API 요청중 에러 발생 : {}", e.getMessage());
			throw new CustomException(ErrorCode.GPT_REQUEST_ERROR);
		}

		if (response == null) {
			log.warn("GPT API 응답 BODY 없음");
			throw new CustomException(ErrorCode.GPT_REQUEST_BODY_NULL);
		}

		log.debug("GPT API 요청 정상 종료");
		return response;
	}
}
