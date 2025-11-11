package com.example.springwebauthn4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.springwebauthn4j.util.SecurityContextUtil;

@Component
public class UsernameAuthenticationProvider extends DaoAuthenticationProvider {
	
	@Override
	public void doAfterPropertiesSet() {
	}
	
	@Override
	public boolean supports(Class<?> authentication) {
		if (SecurityContextUtil.isUsernameAuthenticated()) {
			return false;
		}
		return super.supports(authentication);
	}
	
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {
	}
}