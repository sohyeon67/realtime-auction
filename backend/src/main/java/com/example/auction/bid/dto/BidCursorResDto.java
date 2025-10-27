package com.example.auction.bid.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BidCursorResDto {

    private List<BidResDto> bids;
    private boolean hasNext;
    private Long lastBidId;
}
