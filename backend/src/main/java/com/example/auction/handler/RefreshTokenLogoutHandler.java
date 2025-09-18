package com.example.auction.handler;

import com.example.auction.exception.InvalidTokenException;
import com.example.auction.jwt.service.JwtService;
import com.example.auction.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class RefreshTokenLogoutHandler implements LogoutHandler {

    private final JwtService jwtService;
    private final JwtTokenProvider jwtTokenProvider;

    public RefreshTokenLogoutHandler(JwtService jwtService, JwtTokenProvider jwtTokenProvider) {
        this.jwtService = jwtService;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    /**
     * LogoutFilter가 요청을 가로챈 후 핸들러들을 실행
     * 여기서 로그아웃 로직을 수행한다.
     * LogoutSuccessHandler를 따로 설정하지 않아서
     * SecurityConfig의 authenticationEntryPoint 설정에 의해 401 응답을 내려준다.
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

            if (!StringUtils.hasText(body)) return;

            // JSON 파싱
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);

            // refreshToken 추출
            String refreshToken = jsonNode.path("refreshToken").asText();
            if (!StringUtils.hasText(refreshToken)) return;

            // 유효성 검증
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                return;
            }

            // 특정 Refresh Token 삭제
            jwtService.removeRefresh(refreshToken);

        } catch (IOException e) {
            throw new RuntimeException("Refresh Token 읽기 실패", e);
        }
    }

}
