package com.example.springwebauthn4j;

import com.example.springwebauthn4j.util.SecurityContextUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import java.io.IOException;

public class UsernameAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final String nextAuthUrl;

    public UsernameAuthenticationSuccessHandler(String nextAuthUrl, String defaultTargetUrl) {
        super(defaultTargetUrl);
        this.nextAuthUrl = nextAuthUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        if (SecurityContextUtil.isUsernameAuthenticated()) {
            response.sendRedirect(nextAuthUrl);
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}