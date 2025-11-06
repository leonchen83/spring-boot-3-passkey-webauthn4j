// java
package com.example.springwebauthn4j;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class PasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public PasswordAuthenticationFilter(String pattern, String httpMethod) {
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(pattern, httpMethod));
    }
}