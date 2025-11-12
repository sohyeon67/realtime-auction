package com.example.auction.auction.dto;

import com.example.auction.auction.domain.AuctionStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AuctionSearchCond {

    private String keyword;
    private List<Long> categoryIds;
    private String sellerName;
    private Long minPrice;
    private Long maxPrice;
    private AuctionStatus status = AuctionStatus.ONGOING;

    // 내 경매 조회용
    private Boolean my;
    private Long sellerId;
}
