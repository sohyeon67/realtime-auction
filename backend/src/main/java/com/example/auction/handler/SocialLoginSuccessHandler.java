package com.example.auction.handler;

import com.example.auction.jwt.repository.RefreshTokenRepository;
import com.example.auction.jwt.service.JwtService;
import com.example.auction.member.repository.MemberRepository;
import com.example.auction.util.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Spring Security OAuth2 Client + SuccessHandler 구조 => redirect 응답 이용
 *
 * 로그인 성공 -> 우리쪽으로 리다이렉트 -> 필터가 가로채서 리소스 서버로부터 정보 획득 ->
 * OAuth2UserDetailsService에서 DB 저장 및 OAuth2UserDetails 반환
 * -> 성공 핸들러 실행. jwt 토큰 생성
 */
@Slf4j
@Component
@Qualifier("SocialLoginSuccessHandler")
public class SocialLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtService jwtService;

    public SocialLoginSuccessHandler(JwtTokenProvider jwtTokenProvider, MemberRepository memberRepository, JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("========= 소셜 로그인 성공 핸들러 =========");
        String username = authentication.getName();

        // Refresh Token 발급
        String refreshToken = jwtTokenProvider.createRefreshToken(username);

        // 발급한 Refresh DB 테이블 저장 (Refresh whitelist)
        jwtService.addRefresh(username, refreshToken);

        // 헤더 방식 전달 -> 리다이렉트 응답의 경우 브라우저가 자동으로 리다이렉트 처리(Location 헤더)하므로 프론트 js가 응답 헤더를 읽을 수 없다.
        // JSON body 전달 -> 리다이렉트 응답이므로 바디를 쓸 수 없음
        // 쿼리 파라미터 전달 -> URL에 토큰이 노출되고 히스토리에 남아 보안 취약
        // 쿠키 방식 - 자동 전송 사용하기
        response.addCookie(createCookie("refreshToken", refreshToken));
        response.sendRedirect("http://localhost:5173/auth/callback"); // 소셜로그인 성공 후 쿠키를 받아서 백엔드 쪽으로 쿠키를 헤더로 바꾸도록함
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true); // js 쿠키 접근 불가
        cookie.setSecure(false); // true이면 https에서만
        cookie.setPath("/");
        cookie.setMaxAge(10); // 10초 (프론트에서 발급 후 바로 헤더 전환 로직 진행 예정)
        return cookie;
    }
}
