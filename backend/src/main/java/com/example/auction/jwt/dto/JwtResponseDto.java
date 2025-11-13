package com.example.auction.jwt.dto;

/**
 * RefreshToken은 쿠키 기반
 * AccessToken만 DTO로 전달
 */
public record JwtResponseDto(String accessToken) {
}
