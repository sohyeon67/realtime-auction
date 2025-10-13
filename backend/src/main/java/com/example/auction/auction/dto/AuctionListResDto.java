package com.example.auction.auction.dto;

import com.example.auction.auction.domain.AuctionStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AuctionListResDto {

    private Long auctionId;
    private String mainImageUrl;
    private String title;
    private Integer currentPrice;
    private int bidCount;
    private LocalDateTime endTime;

    private AuctionStatus status;

    private String sellerNickname;

    @QueryProjection
    public AuctionListResDto(Long auctionId, String mainImageUrl, String title, Integer currentPrice, int bidCount, LocalDateTime endTime, AuctionStatus status, String sellerNickname) {
        this.auctionId = auctionId;
        this.mainImageUrl = mainImageUrl;
        this.title = title;
        this.currentPrice = currentPrice;
        this.bidCount = bidCount;
        this.endTime = endTime;
        this.status = status;
        this.sellerNickname = sellerNickname;
    }
}
