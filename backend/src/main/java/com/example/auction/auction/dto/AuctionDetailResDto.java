package com.example.auction.auction.dto;

import com.example.auction.auction.domain.AuctionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 경매 상세 페이지 응답 dto
 */
@Getter
@AllArgsConstructor
@Builder
public class AuctionDetailResDto {

    private Long auctionId;
    private String title;

    private Long categoryId;
    private String categoryName;

    private String description;

    private int startPrice;
    private int currentPrice;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;

    private Boolean canEdit;

    private List<AuctionImageResDto> images;

}
