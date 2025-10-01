package com.example.auction.auction.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 경매 수정 요청 dto
 */
@Getter
@Setter
public class AuctionUpdateReqDto {

    @NotBlank(message = "제목을 입력하세요")
    private String title;

    @NotNull(message = "카테고리를 지정해주세요")
    private Long categoryId;

    @NotBlank(message = "내용을 입력하세요")
    private String description;

    @NotNull(message = "시작 가격을 입력해주세요")
    @Min(value = 1000, message = "시작 가격은 1000원 이상이어야 합니다")
    @Max(value = 10_000_000, message = "시작 가격은 1000만원 이하여아 합니다")
    private Integer startPrice;

    @NotNull(message = "시작 시간을 지정해주세요")
    private LocalDateTime startTime;

    @NotNull(message = "종료 시간을 지정해주세요")
    private LocalDateTime endTime;

    // 기존 이미지 파일
    private List<AuctionImageReqDto> existFiles = new ArrayList<>();

    // 새로 추가한 이미지 파일
    private List<AuctionImageReqDto> newFiles = new ArrayList<>();



}
