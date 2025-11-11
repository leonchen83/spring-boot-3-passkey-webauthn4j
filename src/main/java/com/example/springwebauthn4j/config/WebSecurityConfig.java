package com.example.springwebauthn4j.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import com.example.springwebauthn4j.CustomAuthenticationFailureHandler;
import com.example.springwebauthn4j.Fido2AuthenticationFilter;
import com.example.springwebauthn4j.Fido2AuthenticationProvider;
import com.example.springwebauthn4j.PasswordAuthenticationFilter;
import com.example.springwebauthn4j.PasswordAuthenticationProvider;
import com.example.springwebauthn4j.UsernameAuthenticationFilter;
import com.example.springwebauthn4j.UsernameAuthenticationProvider;
import com.example.springwebauthn4j.UsernameAuthenticationSuccessHandler;
import com.example.springwebauthn4j.service.SampleUserDetailsService;
import com.example.springwebauthn4j.util.SecurityContextUtil;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	
	private final UsernameAuthenticationProvider usernameAuthenticationProvider;
	private final PasswordAuthenticationProvider passwordAuthenticationProvider;
	private final Fido2AuthenticationProvider fido2AuthenticationProvider;
	private final SampleUserDetailsService userDetailsService;
	
	@Autowired
	public WebSecurityConfig(
			UsernameAuthenticationProvider usernameAuthenticationProvider,
			PasswordAuthenticationProvider passwordAuthenticationProvider,
			Fido2AuthenticationProvider fido2AuthenticationProvider,
			SampleUserDetailsService userDetailsService) {
		this.usernameAuthenticationProvider = usernameAuthenticationProvider;
		this.passwordAuthenticationProvider = passwordAuthenticationProvider;
		this.fido2AuthenticationProvider = fido2AuthenticationProvider;
		this.userDetailsService = userDetailsService;
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
						.requestMatchers("/login", "/login-fido2", "/authenticate/option").permitAll()
						.requestMatchers("/password").hasAnyAuthority(SecurityContextUtil.Auth.AUTHENTICATED_USERNAME.getValue())
						.requestMatchers("/**").hasRole(SecurityContextUtil.Role.USER.name())
				)
				.formLogin(form -> form.loginPage("/login").permitAll())
				.addFilterAt(createUsernameAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
				.addFilterAt(createPasswordAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
				.addFilterAt(createFido2AuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
				.csrf(csrf -> csrf.ignoringRequestMatchers("/authenticate/option", "/register/option", "/register/verify"))
				.headers(headers -> headers.frameOptions(frame -> frame.disable()))
				.authenticationManager(authenticationManager);
		
		return http.build();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		return createAuthenticationManagerBuilder(http).build();
	}
	
	private AuthenticationManagerBuilder createAuthenticationManagerBuilder(HttpSecurity http) throws Exception {
		AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
		configure(auth);
		return auth;
	}
	
	private void configure(AuthenticationManagerBuilder auth) {
		// set UserDetailsService on providers
		usernameAuthenticationProvider.setUserDetailsService(userDetailsService);
		passwordAuthenticationProvider.setUserDetailsService(userDetailsService);
		
		// register providers
		auth.authenticationProvider(usernameAuthenticationProvider);
		auth.authenticationProvider(passwordAuthenticationProvider);
		auth.authenticationProvider(fido2AuthenticationProvider);
	}
	
	@Bean
	public UsernamePasswordAuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager) {
		return createPasswordAuthenticationFilter(authenticationManager);
	}
	
	private UsernamePasswordAuthenticationFilter createUsernameAuthenticationFilter(AuthenticationManager authenticationManager) {
		UsernameAuthenticationFilter filter = new UsernameAuthenticationFilter("/login", "POST");
		filter.setSecurityContextRepository(new DelegatingSecurityContextRepository(new HttpSessionSecurityContextRepository()));
		filter.setAuthenticationManager(authenticationManager);
		filter.setAuthenticationSuccessHandler(new UsernameAuthenticationSuccessHandler("/password", "/mypage"));
		filter.setAuthenticationFailureHandler(new CustomAuthenticationFailureHandler("/login?error"));
		return filter;
	}
	
	private PasswordAuthenticationFilter createPasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
		PasswordAuthenticationFilter filter = new PasswordAuthenticationFilter("/password", "POST");
		filter.setSecurityContextRepository(new DelegatingSecurityContextRepository(new HttpSessionSecurityContextRepository()));
		filter.setAuthenticationManager(authenticationManager);
		filter.setAuthenticationSuccessHandler(new org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler("/mypage"));
		filter.setAuthenticationFailureHandler(new CustomAuthenticationFailureHandler("/login?error"));
		return filter;
	}
	
	private Fido2AuthenticationFilter createFido2AuthenticationFilter(AuthenticationManager authenticationManager) {
		Fido2AuthenticationFilter filter = new Fido2AuthenticationFilter("/login-fido2", "POST");
		filter.setSecurityContextRepository(new DelegatingSecurityContextRepository(new HttpSessionSecurityContextRepository()));
		filter.setAuthenticationManager(authenticationManager);
		filter.setAuthenticationSuccessHandler(new org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler("/mypage"));
		filter.setAuthenticationFailureHandler(new CustomAuthenticationFailureHandler("/login?error"));
		return filter;
	}
}