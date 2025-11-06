// java
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
		// NOP: 防止 "A UserDetailsService must be set" 错误，UserDetailsService 在配置类中设置
	}
	
	@Override
	public boolean supports(Class<?> authentication) {
		// 如果已通过用户名认证则不由此 provider 处理
		if (SecurityContextUtil.isUsernameAuthenticated()) {
			return false;
		}
		return super.supports(authentication);
	}
	
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {
		// NOP: 不进行密码校验
	}
}