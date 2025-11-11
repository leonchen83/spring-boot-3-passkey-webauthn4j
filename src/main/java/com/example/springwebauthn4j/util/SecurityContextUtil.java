package com.example.springwebauthn4j.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class SecurityContextUtil {
	
	public static User getLoginUser() {
		if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) {
			return null;
		}
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return (principal instanceof User) ? (User) principal : null;
	}
	
	public static boolean isUsernameAuthenticated() {
		User user = getLoginUser();
		if (user == null) {
			return false;
		}
		return user.getAuthorities().stream().anyMatch(a -> Auth.AUTHENTICATED_USERNAME.getValue().equals(a.getAuthority()));
	}
	
	public enum Auth {
		AUTHENTICATED_USERNAME("authenticated-username"), AUTHENTICATED_PASSWORD("authenticated-password"), AUTHENTICATED_FIDO("authenticated-fido");
		
		private final String value;
		
		Auth(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}
	
	public enum Role {
		USER("ROLE_USER");
		
		private final String value;
		
		Role(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}
}