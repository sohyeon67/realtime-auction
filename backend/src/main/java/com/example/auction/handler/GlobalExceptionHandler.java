package com.example.auction.handler;

import com.example.auction.exception.CookieNotFoundException;
import com.example.auction.exception.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 쿠키 없음
    @ExceptionHandler(CookieNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCookieNotFound(CookieNotFoundException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", e.getMessage()));
    }


    // 토큰 유효성
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidToken(InvalidTokenException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401
                .body(Map.of("error", e.getMessage()));
    }

    // 유저 정보가 없음
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFound(UsernameNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404
                .body(Map.of("error", e.getMessage()));
    }

    // 권한 없음
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN) // 403
                .body(Map.of("error", "접근 권한이 없습니다."));
    }

    // Bean Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(Map.of("errors", errors));
    }

    // 입력값 문제
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(Map.of("error", e.getMessage()));
    }

    // 나머지
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }

}
