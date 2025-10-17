package com.example.auction.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:5173")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // "/app"로 시작하는 url패턴으로 메시지가 발행되면 @MessageMapping이 붙은 메서드로 라우팅
        registry.setApplicationDestinationPrefixes("/app");

        // 클라이언트가 "/topic"으로 시작하는 목적지로 메시지를 구독(subscribe)하면
        // 메모리 기반 메시지 브로커가 해당 메시지를 브로드캐스트한다.
        registry.enableSimpleBroker("/topic"); // 내장브로커
    }

    // 웹소켓요청(connect, subscribe, disconnect) 등의 요청시에는 http header 등 http 메시지를 넣어올 수 있고,
    // 이를 interceptor를 통해 가로채 토큰 등을 검증할 수 있다.
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler); // preSend 메서드로 들어감
    }
}
