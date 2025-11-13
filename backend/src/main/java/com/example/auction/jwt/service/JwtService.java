package com.example.auction.jwt.service;

import com.example.auction.exception.InvalidTokenException;
import com.example.auction.jwt.domain.RefreshToken;
import com.example.auction.jwt.dto.JwtResponseDto;
import com.example.auction.jwt.repository.RefreshTokenRepository;
import com.example.auction.member.domain.RoleType;
import com.example.auction.util.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.auction.util.CookieUtils.addRefreshTokenToCookie;
import static com.example.auction.util.CookieUtils.extractRefreshTokenFromCookie;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // JWT Refresh 토큰 발급 후 저장 메소드
    @Transactional
    public void addRefresh(String username, String refreshToken) {
        RefreshToken entity = RefreshToken.builder()
                .username(username)
                .refresh(refreshToken)
                .build();

        refreshTokenRepository.save(entity);
    }

    // JWT Refresh 존재 확인 메소드
    @Transactional(readOnly = true)
    public Boolean existsRefresh(String username, String refreshToken) {
        return refreshTokenRepository.existsByUsernameAndRefresh(username, refreshToken);
    }

    // JWT Refresh 토큰 삭제 메소드
    @Transactional
    public void removeRefresh(String refreshToken) {
        refreshTokenRepository.deleteByRefresh(refreshToken);
    }

    // 특정 유저 Refresh 토큰 모두 삭제 (탈퇴)
    @Transactional
    public void removeRefreshUser(String username) {
        refreshTokenRepository.deleteByUsername(username);
    }

    /**
     * 소셜 로그인 성공 후 임시 쿠키(Refresh) -> Access Token, Refresh Token 둘 다 발급
     */
    @Transactional
    public JwtResponseDto exchangeToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null || !validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 정보 추출
        String username = jwtTokenProvider.extractUsername(refreshToken);
        String role = RoleType.USER.name(); // 기본값

        return rotateTokens(refreshToken, username, role, response);
    }

    /**
     * Access Token 만료 시 클라이언트는 Refresh Token을 보내서 갱신한다.
     * 항상 로그인이 유지될 수 있도록 같이 발급한다.
     */
    @Transactional
    public JwtResponseDto refreshRotate(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null || !validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException("유효하지 않은 리프레시 토큰입니다."); // 커스텀 예외 만들어서 프론트에서 로그인으로 넘기도록 하기
        }

        // 정보 추출
        String username = jwtTokenProvider.extractUsername(refreshToken);
        String role = jwtTokenProvider.extractRole(refreshToken); // refresh token에 role 저장안해서 null... 이부분은 다시 생각해보기

        return rotateTokens(refreshToken, username, role, response);
    }

    /**
     * 로그인, refresh 공통 처리용 편의 메서드
     * AccessToken과 RefreshToken 새로 발급
     * DB 저장, 쿠키 세팅과 같은 비즈니스 로직 담당
     */
    @Transactional
    public JwtResponseDto rotateTokens(String refreshToken, String username, String role, HttpServletResponse response) {
        // 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(username, role);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(username);

        // 기존 Refresh 토큰 DB 삭제 후 신규 추가
        if (refreshToken != null) {
            refreshTokenRepository.deleteByRefresh(refreshToken);
        }
        refreshTokenRepository.save(RefreshToken.builder()
                .username(username)
                .refresh(newRefreshToken)
                .build()); // 새 토큰 저장

        // 새 refresh token을 HttpOnly 쿠키로 응답에 담기
        addRefreshTokenToCookie(response, newRefreshToken);

        return new JwtResponseDto(newAccessToken);
    }


    /**
     * 리프레시 토큰 검증 및 DB 확인
     * 액세스 토큰이 넘어와도 여기서 막힘
     */
    private boolean validateRefreshToken(String token) {
        try {
            String username = jwtTokenProvider.extractUsername(token);
            return refreshTokenRepository.existsByUsernameAndRefresh(username, token);
        } catch (JwtException e) {
            return false;
        }
    }

}
