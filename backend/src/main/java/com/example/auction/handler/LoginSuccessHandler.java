package com.example.auction.handler;

import com.example.auction.jwt.service.JwtService;
import com.example.auction.util.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Qualifier("LoginSuccessHandler")
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginSuccessHandler(JwtService jwtService, JwtTokenProvider jwtTokenProvider) {
        this.jwtService = jwtService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().toString();

        // JWT(Access/Refresh) 발급
        String accessToken = jwtTokenProvider.createAccessToken(username, role);
        String refreshToken = jwtTokenProvider.createRefreshToken(username);

        // 발급한 Refresh Token DB 저장 (화이트리스트)
        jwtService.addRefresh(username, refreshToken);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = String.format("{\"accessToken\":\"%s\", \"refreshToken\":\"%s\"}", accessToken, refreshToken);
        response.getWriter().write(json);
        response.getWriter().flush();
    }
}
