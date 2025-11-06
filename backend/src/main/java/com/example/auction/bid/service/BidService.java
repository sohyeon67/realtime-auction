package com.example.auction.bid.service;

import com.example.auction.auction.domain.Auction;
import com.example.auction.bid.domain.Bid;
import com.example.auction.bid.dto.BidCursorResDto;
import com.example.auction.bid.dto.BidResDto;
import com.example.auction.bid.repository.BidRepository;
import com.example.auction.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

        // 입찰수 증가
        auction.increaseBidCount();

        return BidResDto.builder()
                .bidId(saved.getId())
                .bidderName(saved.getMember().getNickname())
                .bidPrice(saved.getBidPrice())
                .bidTime(saved.getCreatedAt())
                .build();

    }

    @Transactional(readOnly = true)
    public BidCursorResDto findBidsByAuctionId(Long auctionId, Long lastBidId, int size) {
        // size+1개 조회
        List<Bid> bids = bidRepository.findNextBidsByBidId(auctionId, lastBidId, PageRequest.of(0, size + 1));

        // 다음 페이지가 있는지
        boolean hasNext = bids.size() > size;

        // 실제 반환할 데이터
        List<Bid> resultBids = hasNext ? bids.subList(0, size) : bids;

        // 엔티티 -> DTO 변환
        List<BidResDto> bidDtos = resultBids.stream()
                .map(bid -> BidResDto.builder()
                        .bidId(bid.getId())
                        .bidderName(bid.getMember().getNickname())
                        .bidPrice(bid.getBidPrice())
                        .bidTime(bid.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        // 마지막 커서
        Long newLastBidId = bids.isEmpty() ? null : resultBids.get(resultBids.size() - 1).getId();

        return new BidCursorResDto(bidDtos, hasNext, newLastBidId);
    }
}
