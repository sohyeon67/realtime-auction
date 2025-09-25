package com.example.auction.auction.dto;

import com.example.auction.auction.domain.Auction;
import com.example.auction.auction.domain.AuctionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class AuctionResDto {

    private Long auctionId;
    private String title;
    private String categoryName;
    private String description;
    private Long sellerId;
    private String sellerNickname;
    private int startPrice;
    private int currentPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;

    public static AuctionResDto fromEntity(Auction auction) {
        return AuctionResDto.builder()
                .auctionId(auction.getId())
                .title(auction.getTitle())
                .categoryName(auction.getCategory().getName())
                .description(auction.getDescription())
                .sellerId(auction.getSeller().getId())
                .sellerNickname(auction.getSeller().getNickname())
                .startPrice(auction.getStartPrice())
                .currentPrice(auction.getCurrentPrice())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .status(auction.getStatus())
                .build();
    }
}
