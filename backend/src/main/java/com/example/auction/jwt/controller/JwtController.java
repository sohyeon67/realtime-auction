package com.example.auction.jwt.controller;

import com.example.auction.jwt.dto.JwtResponseDto;
import com.example.auction.jwt.dto.RefreshTokenReqDto;
import com.example.auction.jwt.service.JwtService;
import com.example.auction.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 로그인, 토큰 발급/갱신 등 인증 관련 API
 */
@RestController
@RequiredArgsConstructor
public class JwtController {

    private final JwtService jwtService;
    private final JwtTokenProvider jwtTokenProvider;

    // 소셜 로그인 쿠키 방식의 Refresh 토큰 헤더 방식으로 교환
    @PostMapping("/jwt/exchange")
    public JwtResponseDto jwtExchange(HttpServletRequest request, HttpServletResponse response) {
        return jwtService.cookie2Header(request, response);
    }

    // Refresh Token으로 Access Token 재발급 (Rotate 포함)
    @PostMapping("/jwt/refresh")
    public JwtResponseDto jwtRefreshApi(@RequestBody RefreshTokenReqDto dto) {
        return jwtService.refreshRotate(dto);
    }
}
