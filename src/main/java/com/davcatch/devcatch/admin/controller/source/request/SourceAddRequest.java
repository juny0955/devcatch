package com.davcatch.devcatch.admin.controller.source.request;

import com.davcatch.devcatch.domain.source.ParseMethod;
import com.davcatch.devcatch.domain.source.Source;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SourceAddRequest {

	@NotBlank(message = "소스 이름은 필수 입력값입니다.")
	private String name;

	@NotBlank(message = "메인 URL은 필수 입력값입니다.")
	private String mainUrl;

	@NotBlank(message = "피드 URL은 필수 입력값입니다.")
	private String feedUrl;

	@NotNull(message = "파싱 방법은 필수 선택값입니다.")
	private ParseMethod parseMethod;

	private boolean useLink;
	private boolean isForeign;
	private boolean isActive;

	public Source toEntity() {
		return Source.builder()
			.name(name)
			.mainUrl(mainUrl)
			.feedUrl(feedUrl)
			.parseMethod(parseMethod)
			.useLink(useLink)
			.isForeign(isForeign)
			.isActive(isActive)
			.build();
	}
}