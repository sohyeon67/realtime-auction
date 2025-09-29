package com.example.auction.auction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 경매 생성, 및 수정 완료 후 응답 dto
 */
@Getter
@AllArgsConstructor
public class AuctionIdResDto {

    private Long auctionId;
}
