package com.example.springwebauthn4j;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.stereotype.Component;

@Component
public class PasswordAuthenticationProvider extends DaoAuthenticationProvider {
	
	@Override
	public void doAfterPropertiesSet() {
		// no-op: skip parent checks
	}
}