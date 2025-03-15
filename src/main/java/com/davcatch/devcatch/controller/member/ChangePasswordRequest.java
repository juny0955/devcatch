package com.davcatch.devcatch.controller.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

	@NotBlank(message = "현재 비밀번호는 필수 입력값 입니다.")
	private String currentPassword;

	@NotBlank(message = "변경할 비밀번호는 필수 입력값 입니다")
	@Size(min = 8, message = "변경할 비밀번호는 8자리 이상이어야 합니다.")
	private String newPassword;

	@NotBlank(message = "변경할 비민번호 검증은 필수 입력값 입니다.")
	private String confirmNewPassword;
}
