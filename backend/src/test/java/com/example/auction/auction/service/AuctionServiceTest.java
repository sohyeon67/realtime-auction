package com.example.auction.auction.service;

import com.example.auction.auction.domain.Auction;
import com.example.auction.auction.domain.AuctionStatus;
import com.example.auction.auction.repository.AuctionRepository;
import com.example.auction.bid.domain.Bid;
import com.example.auction.bid.repository.BidRepository;
import com.example.auction.category.domain.Category;
import com.example.auction.category.repository.CategoryRepository;
import com.example.auction.config.IntegrationTest;
import com.example.auction.member.domain.Member;
import com.example.auction.member.domain.MemberStatus;
import com.example.auction.member.domain.RoleType;
import com.example.auction.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@IntegrationTest
class AuctionServiceTest {

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BidRepository bidRepository;

    private Member seller;
    private List<Member> bidders;
    private Auction auction;

    @BeforeEach
    void setUp() {
        // --- 테스트용 카테고리 하나 생성 ---
        Category category = Category.builder()
                .name("테스트 카테고리")
                .priority(0)
                .depth(0)
                .build();
        categoryRepository.save(category);

        // --- 판매자 생성 ---
        seller = Member.builder()
                .username("seller1")
                .password("1234")
                .name("판매자 이름")
                .nickname("판매자")
                .phone("01012345678")
                .role(RoleType.USER)
                .isSocial(false)
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(seller);

        // --- 입찰자 여러 명 생성 (예: 100명) ---
        bidders = IntStream.rangeClosed(1, 100)
                .mapToObj(i -> Member.builder()
                        .username("bidder" + i)
                        .password("1234")
                        .name("입찰자" + i)
                        .nickname("입찰자" + i)
                        .phone("010" + String.format("%08d", i))
                        .role(RoleType.USER)
                        .isSocial(false)
                        .status(MemberStatus.ACTIVE)
                        .build())
                .map(memberRepository::save)
                .toList();


        // --- 경매 생성 ---
        auction = Auction.builder()
                .title("동시성 테스트 경매")
                .description("100명이 동시에 입찰하는 테스트")
                .seller(seller)
                .category(category)
                .startPrice(10000L)
                .currentPrice(10000L)
                .startTime(LocalDateTime.now().minusMinutes(10))
                .endTime(LocalDateTime.now().plusMinutes(10))
                .status(AuctionStatus.ONGOING)
                .bidCount(0)
                .build();
        auctionRepository.save(auction);
    }

    /**
     * AuctionService의 placeBid가
     * 비관적 락으로 경매를 조회할 경우 동시성 문제 해결됨
     * 비관적 락이 아닌 일반 조회의 경우 동시성 문제가 발생함
     */
    @Test
    @DisplayName("동시성 테스트 - 여러 입찰자가 동시에 입찰 시")
    void doesNotControlConcurrency() throws InterruptedException {
        log.info("================ 동시성 테스트 실행 시작 ================");

        // given
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            executorService.submit(() -> {
                Member bidder = bidders.get(index);
                String username = bidder.getUsername();
                long bidPrice = 10000L + (index + 1) * 1000L;

                try {
                    log.info("{}가 {}원으로 입찰 시도", bidder.getUsername(), bidPrice);
                    auctionService.placeBid(auction.getId(), bidder.getUsername(), bidPrice);
                } catch (Exception e) {
                    log.warn("{} 입찰 실패: {}", username, e.getMessage());
                } finally {
                    // 성공/실패 관계없이 작업 완료
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);  // 최대 10초 기다림
        executorService.shutdown();

        // then
        Auction result = auctionRepository.findById(auction.getId()).orElseThrow();

        int actualBidCount = bidRepository.countByAuctionId(auction.getId());
        Long highestBidPrice = bidRepository.findTopByAuctionIdOrderByBidPriceDesc(auction.getId())
                .map(Bid::getBidPrice)
                .orElse(auction.getStartPrice());

        // 결과 출력
        log.info("실제 입찰수 입찰 수 = {}", actualBidCount);
        log.info("실제 최고가 경매가 = {}", highestBidPrice);

        log.info("최종 저장된 입찰 수 = {}", result.getBidCount());
        log.info("최종 저장된 경매가 = {}", result.getCurrentPrice());

        assertEquals(highestBidPrice, result.getCurrentPrice());
        assertEquals(actualBidCount, result.getBidCount());

        log.info("================ 동시성 테스트 실행 끝 ================");
    }
}