package com.example.auction.auction.scheduler;

import com.example.auction.auction.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionStatusScheduler {

    private final AuctionRepository auctionRepository;

    @Scheduled(cron = "0 0/5 * * * *")
    @Transactional
    public void updateAuctionStatus() {
        LocalDateTime now = LocalDateTime.now();

        int started = auctionRepository.startReadyAuctions(now);
        int ended = auctionRepository.endOngoingAuctions(now);

        log.info("경매 상태 갱신 완료 : 시작됨 {}건, 종료됨 {}건", started, ended);
    }
}
