package com.davcatch.devcatch.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegRequest {

	@NotBlank(message = "이름은 필수 입력값 입니다.")
	private String name;

	@NotBlank
	@Email
	private String email;
}
