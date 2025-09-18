package com.example.auction.filter;

import com.example.auction.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Authorization 헤더에 담긴 토큰 검증
 * UsernamePasswordAuthenticationFilter 앞에 두어서 인증 과정을 대신 수행하도록 하는 커스텀 JWT 인증 필터
 * DB 조회 없이 토큰 검증
 * 스프링 빈이 아니라 스프링 시큐리티에서 직접 객체를 생성하도록 함
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter { // 하나의 요청에 대해 한 번만 실행되는 필터. 중복 실행 방지

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Authorization 헤더
        String authHeader = request.getHeader("Authorization");

        // jwt 토큰이 없는 경우
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // 권한이 필요없는 요청도 있기 때문에 다음 필터로 넘김
            return;
        }

        String jwt = authHeader.substring(7);

        if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"토큰이 만료되었거나 유효하지 않습니다.\"}"); // 다음 필터로 넘기지 않는다.
        }

    }
}
