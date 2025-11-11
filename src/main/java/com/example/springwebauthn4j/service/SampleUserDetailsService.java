package com.example.springwebauthn4j.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.springwebauthn4j.repository.MuserRepository;
import com.example.springwebauthn4j.util.SecurityContextUtil;

@Service
public class SampleUserDetailsService implements UserDetailsService {
	
	private final MuserRepository mUserRepository;
	
	public SampleUserDetailsService(MuserRepository mUserRepository) {
		this.mUserRepository = mUserRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		if (userId == null || userId.isEmpty()) {
			throw new UsernameNotFoundException("userId is null or empty");
		}
		
		var mUser = mUserRepository.findByUserId(userId);
		if (mUser == null) {
			throw new UsernameNotFoundException("Not found userId");
		}
		
		List<SimpleGrantedAuthority> authorities;
		if (SecurityContextUtil.isUsernameAuthenticated()) {
			authorities = Arrays.asList(new SimpleGrantedAuthority(SecurityContextUtil.Auth.AUTHENTICATED_PASSWORD.getValue()), new SimpleGrantedAuthority(SecurityContextUtil.Role.USER.getValue()));
		} else {
			authorities = Arrays.asList(new SimpleGrantedAuthority(SecurityContextUtil.Auth.AUTHENTICATED_USERNAME.getValue()));
		}
		
		return new User(mUser.getUserId(), mUser.getPassword(), authorities);
	}
}