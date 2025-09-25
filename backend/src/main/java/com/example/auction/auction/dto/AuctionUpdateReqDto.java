package com.example.auction.auction.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AuctionUpdateReqDto {

    private String title;

    private Long categoryId;

    private String description;

    private LocalDateTime endTime; // READY 상태일 때만 변경 가능
}
