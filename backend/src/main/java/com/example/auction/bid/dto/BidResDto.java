package com.example.auction.bid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BidResDto {

    private Long bidId;
    private String bidderName;
    private Long bidPrice;
    private LocalDateTime bidTime;
}
