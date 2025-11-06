package com.example.springwebauthn4j;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	
	public CustomAuthenticationFailureHandler(String defaultFailureUrl) {
		super(defaultFailureUrl);
	}
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
	                                    HttpServletResponse response,
	                                    AuthenticationException exception)
			throws IOException, ServletException {
		SecurityContextHolder.clearContext();
		
		HttpSession session = request == null ? null : request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		
		super.onAuthenticationFailure(request, response, exception);
	}
}