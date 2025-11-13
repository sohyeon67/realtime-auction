package com.example.auction.handler;

import com.example.auction.jwt.service.JwtService;
import com.example.auction.util.CookieUtils;
import com.example.auction.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.StringUtils;

/**
 * 로그아웃 시 RefreshToken을 쿠키에서 제거하기 위한 핸들러
 * 프론트에서 "/logout"으로 접근
 */
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
        // 쿠키에서 refresh token 읽기
        String refreshToken = CookieUtils.extractRefreshTokenFromCookie(request);
        if (!StringUtils.hasText(refreshToken)) return;

        // 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) return;

        // DB에서 Refresh Token 삭제
        jwtService.removeRefresh(refreshToken);

        // 브라우저 쿠키 삭제
        CookieUtils.deleteRefreshTokenCookie(response);
    }


}
