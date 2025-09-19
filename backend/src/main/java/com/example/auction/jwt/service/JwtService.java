package com.example.auction.jwt.service;

import com.example.auction.exception.CookieNotFoundException;
import com.example.auction.exception.InvalidTokenException;
import com.example.auction.jwt.domain.RefreshToken;
import com.example.auction.jwt.dto.JwtResponseDto;
import com.example.auction.jwt.dto.RefreshTokenReqDto;
import com.example.auction.jwt.repository.RefreshTokenRepository;
import com.example.auction.util.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void removeRefreshUser(String username) {
        refreshTokenRepository.deleteByUsername(username);
    }

    // 소셜 로그인 성공 후 쿠키(Refresh) -> 헤더 방식으로 응답
    @Transactional
    public JwtResponseDto cookie2Header(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키 리스트
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new CookieNotFoundException("쿠키가 존재하지 않습니다.");
        }

        // Refresh 토큰 획득
        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
                break;
            }
        }

        if (refreshToken == null) {
            throw new InvalidTokenException("refreshToken 쿠키가 없습니다.");
        }

        // Refresh 토큰 검증
        Boolean isValid = validateRefreshToken(refreshToken);
        if (!isValid) {
            throw new InvalidTokenException("유효하지 않은 refreshToken입니다.");
        }

        // 정보 추출
        String username = jwtTokenProvider.extractUsername(refreshToken);
        String role = jwtTokenProvider.extractRole(refreshToken);

        // 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(username, role);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(username);

        // 기존 Refresh 토큰 DB 삭제 후 신규 추가
        RefreshToken newRefreshEntity = RefreshToken.builder()
                .username(username)
                .refresh(newRefreshToken)
                .build();

        removeRefresh(refreshToken); // 트랜잭션이 끝날 때까지 delete를 보내지 않고 영속성 컨텍스트에서만 삭제처리
        refreshTokenRepository.flush(); // DB에 삭제 반영
        refreshTokenRepository.save(newRefreshEntity); // 새 토큰 저장

        // 기존 쿠키 제거
        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // https 환경에서는 true
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        return new JwtResponseDto(newAccessToken, newRefreshToken);
    }

    // Access Token 만료 시 클라이언트는 Refresh Token을 보내서 갱신한다.
    // 항상 로그인이 유지될 수 있도록 같이 발급한다.
    @Transactional
    public JwtResponseDto refreshRotate(RefreshTokenReqDto dto) {
        String refreshToken = dto.getRefreshToken();

        // refresh 토큰 검증
        if (!validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException("유효하지 않은 리프레시 토큰입니다."); // 커스텀 예외 만들어서 프론트에서 로그인으로 넘기도록 하기
        }

        // 정보 추출
        String username = jwtTokenProvider.extractUsername(refreshToken);
        String role = jwtTokenProvider.extractRole(refreshToken); // refresh token에 role 저장안해서 null... 이부분은 다시 생각해보기

        // 새로운 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(username, role);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(username);

        // 기존 Refresh 토큰 DB 삭제 후 신규 추가
        RefreshToken newRefreshEntity = RefreshToken.builder()
                .username(username)
                .refresh(newRefreshToken)
                .build();

        refreshTokenRepository.deleteByRefresh(refreshToken);
        refreshTokenRepository.save(newRefreshEntity);

        return new JwtResponseDto(newAccessToken, newRefreshToken);
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
