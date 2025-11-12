package com.example.auction.config;

import com.example.auction.filter.JsonAuthenticationFilter;
import com.example.auction.filter.JwtAuthenticationFilter;
import com.example.auction.handler.RefreshTokenLogoutHandler;
import com.example.auction.jwt.service.JwtService;
import com.example.auction.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * security 관련 빈들을 정의해 둔 설정 클래스
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthenticationSuccessHandler loginSuccessHandler;
    private final AuthenticationSuccessHandler socialLoginSuccessHandler;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtService jwtService;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,
                          @Qualifier("LoginSuccessHandler") AuthenticationSuccessHandler loginSuccessHandler,
                          @Qualifier("SocialLoginSuccessHandler") AuthenticationSuccessHandler socialLoginSuccessHandler,
                          JwtTokenProvider jwtTokenProvider,
                          JwtService jwtService) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.loginSuccessHandler = loginSuccessHandler;
        this.socialLoginSuccessHandler = socialLoginSuccessHandler;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtService = jwtService;
    }

    // 로그인 시 인증을 위한 AuthenticationManager Bean 수동 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 비밀번호 단방향 암호화용 Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 빈 생성
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // jwt 인증/권한 필터가 실행되기 전 cors 허용 처리
                .csrf(AbstractHttpConfigurer::disable) // jwt이므로 csrf 비활성화. 주로 세션+쿠키 방식 사용 시 csrf 토큰 처리 필요
                .formLogin(AbstractHttpConfigurer::disable) // form 기반 인증 필터 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 비활성화. OAuth나 JWT는 Bearer 타입 이용

                .logout(logout -> logout
                        .addLogoutHandler(new RefreshTokenLogoutHandler(jwtService, jwtTokenProvider))
                )

                .oauth2Login(oauth2 -> oauth2
                        .successHandler(socialLoginSuccessHandler))

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 X

                .authorizeHttpRequests(auth -> auth
                        // 이 부분은 컨트롤러(서블릿) 경로를 말한다. 필터 경로 x
                        .requestMatchers("/files/**").permitAll()
                        .requestMatchers("/jwt/exchange", "/jwt/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/members", "/api/members/exist").permitAll()
                        .requestMatchers("/api/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auctions/**").permitAll() // 목록/상세
                        .requestMatchers("/api/auctions/**").authenticated()
                        .requestMatchers("/api/categories/batch").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // 관리자 권한 필요
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().authenticated()) // 로그인 필요(가장 마지막)

                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 401 응답 (로그인 안했을 때)
                        })
                        .accessDeniedHandler(((request, response, accessDeniedException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN); // 403 응답 (로그인했지만 권한이 없을 때)
                        }))
                )

                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), LogoutFilter.class) // jwt 토큰 검증을 위한 필터. 아이디/비밀번호 기반 로그인 처리 전에 JWT 기반 인증 처리
                .addFilterBefore(new JsonAuthenticationFilter(
                        authenticationManager(authenticationConfiguration), loginSuccessHandler), UsernamePasswordAuthenticationFilter.class) // 로그인 요청에 동작
                .build();
    }

    // cors 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // 허용할 프론트엔드 주소
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // 허용 메소드
        configuration.setAllowedHeaders(List.of("*")); // 요청 시 클라이언트가 보낼 수 있는 헤더
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie")); // 서버가 응답 시 프론트에서 읽을 수 있는 헤더
        configuration.setAllowCredentials(true); // 클라이언트가 쿠키, Authorization 헤더를 보낼 수 있게 허용
        configuration.setMaxAge(3600L); // Preflight 요청(OPTIONS) 캐시 유지 시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 위에서 정의한 cors 정책 허용
        return source;
    }
}
