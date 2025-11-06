package com.example.springwebauthn4j.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class H2ConsoleSecurityConfig {
	
	@Bean
	@Order(1)
	public SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
		http
				.securityMatcher(new AntPathRequestMatcher("/h2-console/**"))
				.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
				.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
				.csrf(csrf -> csrf.disable());
		
		return http.build();
	}
	
}