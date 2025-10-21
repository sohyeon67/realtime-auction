package com.example.auction.bid.controller;

import com.example.auction.auction.service.AuctionService;
import com.example.auction.bid.dto.BidReqDto;
import com.example.auction.bid.dto.BidResDto;
import com.example.auction.bid.service.BidService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BidController {

    private final AuctionService auctionService;
    private final BidService bidService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/auctions/{auctionId}/bids")
    public void sendMessage(@DestinationVariable Long auctionId, @Valid @RequestBody BidReqDto dto, Principal principal) {

        // 로그인 안 됐으면 입찰 무시
        if (principal == null) {
            return;
        }

        try {
            // 입찰 처리
            BidResDto bidResDto = auctionService.placeBid(auctionId, principal.getName(), dto.getBidPrice());
            messagingTemplate.convertAndSend("/topic/auctions/" + auctionId, bidResDto);
        } catch (Exception e) {
            // 개인 오류 메시지
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/errors",
                    Map.of("error", e.getMessage()));
        }
    }
}
