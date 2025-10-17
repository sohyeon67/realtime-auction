package com.example.auction.bid.controller;

import com.example.auction.bid.service.BidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/auctions/{auctionId}/bids")
    public String sendMessage(@DestinationVariable Long auctionId, String message, Principal principal) {

        // 경매 시간 검사

        // 입찰 금액이 현재 최고가 + 최소 입찰 단위보다 크거나 같은지

        // 사용자 검사 + 판매자 입찰 방지


        // 웹소켓 발송
        messagingTemplate.convertAndSend("/topic/auctions/" + auctionId, Map.of("testMsg", "입찰성공"));

        // 개인 오류 메시지... user 연동법 확인하기
        if (principal != null) {
            String username = principal.getName();
            messagingTemplate.convertAndSendToUser(username, "/queue/errors", Map.of("testMsg", "입찰실패"));
        }

        return message;
    }
}
