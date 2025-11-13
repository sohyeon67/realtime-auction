package com.example.auction.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt 토큰 생성/검증만 담당
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.access-expiration}")
    private long accessExpiration;
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private Key key;

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        // 비밀키와 알고리즘 정보를 합쳐서 JWT 생성/검증 시 사용하는 Key 객체 만들기
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    // ================ 인증 객체 생성 ================
    public Authentication getAuthentication(String token) {
        String username = extractUsername(token);
        String role = extractRole(token);
        return new UsernamePasswordAuthenticationToken(username, null, Collections.singleton(new SimpleGrantedAuthority(role))); // 인증 객체 생성
    }

    // ================ 토큰 생성 ================
    // AccessToken 발급
    // API 요청 시 Authorization 헤더로 전송하기 위함
    public String createAccessToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // role 포함
        return buildToken(claims, username, accessExpiration);
    }

    // RefreshToken 발급
    // AccessToken 만료 시 재발급용
    public String createRefreshToken(String username) {
        return buildToken(new HashMap<>(), username, refreshExpiration);
    }

    // JWT 토큰 생성
    private String buildToken(Map<String, Object> claims, String subject, long expirationMillis) {
        return Jwts.builder()
                .setClaims(claims) // 클레임 먼저 세팅
                .setSubject(subject)
                .setIssuedAt(new Date()) // 발급일
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis)) // 만료일
                .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘 지정
                .compact();
    }

    // ================ 토큰 검증 ================
    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // JWT 토큰 검증과 Claims 추출을 동시에 수행하는 코드
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        Claims claims = extractClaims(token);
        return claims.getSubject();
    }

    public String extractRole(String token) {
        Claims claims = extractClaims(token);
        return (String) claims.get("role");
    }

}
