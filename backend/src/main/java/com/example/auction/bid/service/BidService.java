package com.example.auction.bid.service;

import com.example.auction.auction.domain.Auction;
import com.example.auction.bid.domain.Bid;
import com.example.auction.bid.dto.BidResDto;
import com.example.auction.bid.repository.BidRepository;
import com.example.auction.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BidService {

    private final BidRepository bidRepository;

    public BidResDto saveBid(Auction auction, Member member, Long bidPrice) {
        // 입찰 생성 및 저장
        Bid bid = Bid.builder()
                .auction(auction)
                .member(member)
                .bidPrice(bidPrice)
                .build();
        Bid saved = bidRepository.save(bid);

        // 경매 현재가 갱신
        auction.updateCurrentPrice(bidPrice);

        return BidResDto.builder()
                .bidId(saved.getId())
                .auctionId(saved.getAuction().getId())
                .bidderName(saved.getMember().getNickname())
                .bidPrice(saved.getBidPrice())
                .bidTime(saved.getCreatedAt())
                .build();

    }
}
