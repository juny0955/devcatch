package com.davcatch.devcatch.gpt;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.exception.ErrorCode;
import com.davcatch.devcatch.gpt.request.GptRequest;
import com.davcatch.devcatch.gpt.response.GptResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GptSummary {

	private static final String URL = "https://api.openai.com/v1/chat/completions";

	private final RestTemplate restTemplate;

	/**
	 * 본문 내용을 사용해 GPT 요약 요청
	 * @param content 본문 내용
	 * @return GPT 요약 내용
	 */
	public String getSummary(String content) throws CustomException {
		log.info("GPT API 요청 시작");

		ResponseEntity<GptResponse> response;

		try {
			HttpEntity<GptRequest> entity = new HttpEntity<>(GptRequest.create(content));
			response = restTemplate.exchange(URL, HttpMethod.POST, entity, GptResponse.class);
		} catch (Exception e) {
			log.info("GPT API 요청중 에러 발생 : {}", e.getMessage());
			throw new CustomException(ErrorCode.GPT_REQUEST_ERROR);
		}

		if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
			log.info("GPT API 요청 정상 종료");
			log.info(response.toString());
			return null;
		}

		log.info("GPT API 응답 BODY 없음");
		throw new CustomException(ErrorCode.GPT_REQUEST_BODY_NULL);
	}
}
