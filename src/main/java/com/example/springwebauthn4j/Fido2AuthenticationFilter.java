package com.example.springwebauthn4j;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Fido2AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public Fido2AuthenticationFilter(String pattern, String httpMethod) {
        super(new AntPathRequestMatcher(pattern, httpMethod));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (request == null || !"POST".equalsIgnoreCase(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + (request == null ? "null" : request.getMethod()));
        }

        String assertion = obtainAssertion(request);
        User principal = obtainPrincipal(request);

        AssertionAuthenticationToken.Fido2Credentials credentials = new AssertionAuthenticationToken.Fido2Credentials(assertion);

        Collection<SimpleGrantedAuthority> authorities = principal.getAuthorities().stream()
                .map(a -> new SimpleGrantedAuthority(a.getAuthority()))
                .collect(Collectors.toList());

        AssertionAuthenticationToken authRequest = new AssertionAuthenticationToken(principal, credentials, authorities);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private String obtainAssertion(HttpServletRequest request) {
        String json = request.getParameter("assertion");
        if (json == null || json.isEmpty()) {
            throw new AuthenticationServiceException("assertion");
        }
        return json;
    }

    private User obtainPrincipal(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return new User("<dmy>", "", Collections.emptyList());
        }
        Object ctx = session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (!(ctx instanceof SecurityContext)) {
            return new User("<dmy>", "", Collections.emptyList());
        }
        Object principal = ((SecurityContext) ctx).getAuthentication() == null ? null : ((SecurityContext) ctx).getAuthentication().getPrincipal();
        if (!(principal instanceof User)) {
            throw new AuthenticationServiceException("assertion");
        }
        return (User) principal;
    }

    private void setDetails(HttpServletRequest request, AssertionAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
}