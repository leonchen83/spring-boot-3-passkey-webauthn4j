package com.example.springwebauthn4j;

import com.example.springwebauthn4j.service.AuthenticateOption;
import com.example.springwebauthn4j.service.AssertionVerifyResult;
import com.example.springwebauthn4j.service.WebAuthnServerService;
import com.example.springwebauthn4j.util.SecurityContextUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Fido2AuthenticationProvider implements AuthenticationProvider {

    private final WebAuthnServerService webAuthnServerService;
    private final HttpServletRequest request;

    public Fido2AuthenticationProvider(WebAuthnServerService webAuthnServerService,
                                       HttpServletRequest request) {
        this.webAuthnServerService = webAuthnServerService;
        this.request = request;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        if (!(authentication instanceof AssertionAuthenticationToken)) {
            throw new BadCredentialsException("Invalid Authentication");
        }

        // 从 session 获取 authenticateOption
        HttpSession session = request == null ? null : request.getSession(false);
        if (session == null) {
            throw new BadCredentialsException("authenticateOption not found");
        }
        Object attr = session.getAttribute("authenticateOption");
        if (!(attr instanceof AuthenticateOption)) {
            throw new BadCredentialsException("authenticateOption not found");
        }
        AuthenticateOption authenticateOption = (AuthenticateOption) attr;

        // 取出 assertion json
        AssertionAuthenticationToken src = (AssertionAuthenticationToken) authentication;
        AssertionAuthenticationToken.Fido2Credentials credentials =
                (AssertionAuthenticationToken.Fido2Credentials) src.getCredentials();
        String publicKeyCredentialGetResultJson = credentials == null ? "" : credentials.getPublicKeyCredentialGetResultJson();
        if (publicKeyCredentialGetResultJson == null || publicKeyCredentialGetResultJson.isEmpty()) {
            throw new BadCredentialsException("Invalid Assertion");
        }

        // 调用服务验证断言
        AssertionVerifyResult verifyResult;
        try {
            verifyResult = webAuthnServerService.verifyAuthenticateAssertion(authenticateOption, publicKeyCredentialGetResultJson);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid Assertion");
        }
        if (verifyResult == null || !verifyResult.isSuccess()) {
            throw new BadCredentialsException("Assertion Verify Failed");
        }

        String userName = verifyResult.getUserId();

        // 构建已认证的 Authentication
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(SecurityContextUtil.Auth.AUTHENTICATED_FIDO.value),
                new SimpleGrantedAuthority(SecurityContextUtil.Role.USER.value)
        );

        User authenticatedPrincipal = new User(userName, "", authorities);
        AssertionAuthenticationToken result = new AssertionAuthenticationToken(authenticatedPrincipal, credentials, authorities);
        result.setAuthenticated(true);
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AssertionAuthenticationToken.class.isAssignableFrom(authentication);
    }
}