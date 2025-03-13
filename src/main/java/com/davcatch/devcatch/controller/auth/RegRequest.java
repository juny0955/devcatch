package com.davcatch.devcatch.controller.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegRequest {

	@NotBlank(message = "이름은 필수 입력값 입니다.")
	private String name;

	@NotBlank(message = "이메일은 필수 입력값 입니다")
	@Email
	private String email;

	@NotBlank(message = "비밀번호는 필수 입력값입니다.")
	private String password;
}
