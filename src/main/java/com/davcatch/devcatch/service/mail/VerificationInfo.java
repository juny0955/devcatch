package com.davcatch.devcatch.service.mail;

import java.time.LocalDateTime;
import java.util.UUID;

import com.davcatch.devcatch.controller.RegRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerificationInfo {

	private String email;
	private String name;
	private LocalDateTime createdAt;

	public static VerificationInfo create(RegRequest request) {
		return VerificationInfo.builder()
			.email(request.getEmail())
			.name(request.getName())
			.createdAt(LocalDateTime.now())
			.build();
	}

	/**
	 * 인증코드 만료 확인
	 * 인증 코드 생성 5분 이후에 만료
	 * @return 만료 여부
	 */
	public boolean isExpired() {
		return LocalDateTime.now().isAfter(createdAt.plusMinutes(5));
	}
}
