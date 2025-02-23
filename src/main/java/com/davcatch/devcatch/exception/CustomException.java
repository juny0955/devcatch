package com.davcatch.devcatch.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends Exception{

	private final ErrorCode errorCode;
}
