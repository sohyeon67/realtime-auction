package com.example.auction.jwt.repository;

import com.example.auction.jwt.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    //Optional<RefreshToken> findByUsername(String username);

    // 탈퇴 시 리프레시 토큰 전부 지우기
    void deleteByUsername(String username);

    // 특정 토큰 지우기
    void deleteByRefresh(String refreshToken);

    // 리프레시 토큰 DB 확인
    boolean existsByUsernameAndRefresh(String username, String token);
}
