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
}
