package com.example.auction.auction.dto;

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
}
