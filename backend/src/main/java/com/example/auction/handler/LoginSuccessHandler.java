package com.example.auction.handler;

import com.example.auction.jwt.dto.JwtResponseDto;
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

/**
 * 자체 로그인 성공 핸들러
 * 프론트에서 "/login"으로 접근
 * Access Token과 Refresh Token을 발급
 * Refresh Token은 HttpOnly 쿠키로 전달
 */
@Component
@Qualifier("LoginSuccessHandler")
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    public LoginSuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().toString();

        // 로그인 시 기존 refresh token 없음 -> null 전달
        // Refresh Token은 쿠키로 발급
        JwtResponseDto jwt = jwtService.rotateTokens(null, username, role, response);

        // Access Token만 JSON으로 프론트에 전달
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = String.format("{\"accessToken\":\"%s\"}", jwt.accessToken());
        response.getWriter().write(json);
        response.getWriter().flush();
    }
}
