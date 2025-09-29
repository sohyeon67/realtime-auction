package com.example.auction.auction.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 판매자는 현재 로그인한 사람의 정보를 가져와 서비스 단에서 지정
 */
@Getter
@Setter
public class AuctionSaveReqDto {

    @NotBlank(message = "제목을 입력하세요")
    private String title;

    @NotNull(message = "카테고리를 지정해주세요")
    private Long categoryId;

    private String description;

    @Min(value = 1000, message = "시작 가격은 1000원 이상이어야 합니다")
    private int startPrice;

    @NotNull(message = "시작 시간을 지정해주세요")
    private LocalDateTime startTime;

    @NotNull(message = "종료 시간을 지정해주세요")
    private LocalDateTime endTime;

    private List<AuctionFileReqDto> files;

}
