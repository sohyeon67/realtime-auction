package com.example.auction.websocket;

import com.example.auction.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    // connect, subscribe, send, disconnect 하기 전에 이 메서드를 탄다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 임시 확인용
        if (accessor != null) {
            String destination = accessor.getDestination();
            log.info("destination {}", destination);
        }

        if (StompCommand.CONNECT == accessor.getCommand()) {
            log.info("connect");

            String bearerToken = accessor.getFirstNativeHeader("Authorization");

            if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.substring(7);
                if (jwtTokenProvider.validateToken(token)) {
                    // 사용자 정보 추출
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);

                    // STOMP 세션에 인증된 사용자 정보 바인딩
                    accessor.setUser(authentication);
                }
            }
        }

        if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            log.info("subscribe");
        }

        if (StompCommand.SEND == accessor.getCommand()) {
            log.info("send");
        }

        return message;
    }
}
