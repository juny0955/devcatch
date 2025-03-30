package com.davcatch.devcatch.common.config.security;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

		String errorMessage = "로그인에 실패했습니다.";

		// 예외 타입에 따라 다른 메시지 설정
		if (exception instanceof BadCredentialsException) {
			errorMessage = "아이디 또는 비밀번호가 맞지 않습니다.";
		} else if (exception instanceof UsernameNotFoundException) {
			errorMessage = "존재하지 않는 회원입니다.";
		} else if (exception instanceof LockedException) {
			errorMessage = "계정이 잠겨있습니다.";
		} else if (exception instanceof DisabledException) {
			errorMessage = "비활성화된 계정입니다.";
		}

		HttpSession session = request.getSession(true);
		if (session != null) {
			session.setAttribute("error", errorMessage);
		}

		response.sendRedirect("/auth/login?error");
	}
}
