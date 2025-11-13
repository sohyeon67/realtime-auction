package com.example.auction.jwt.controller;

import com.example.auction.jwt.dto.JwtResponseDto;
import com.example.auction.jwt.service.JwtService;
import com.example.auction.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 로그인, 토큰 발급/갱신 등 인증 관련 API
 */
@RestController
@RequiredArgsConstructor
public class JwtController {

    private final JwtService jwtService;

    // 소셜 로그인 성공 후 임시 refresh token을 이용하여 토큰들을 발급
    @PostMapping("/jwt/exchange")
    public JwtResponseDto jwtExchange(HttpServletRequest request, HttpServletResponse response) {
        return jwtService.exchangeToken(request, response);
    }

    // Refresh Token으로 Access Token 재발급 (Rotate 포함)
    @PostMapping("/jwt/refresh")
    public JwtResponseDto jwtRefreshApi(HttpServletRequest request, HttpServletResponse response) {
        return jwtService.refreshRotate(request, response);
    }
}
