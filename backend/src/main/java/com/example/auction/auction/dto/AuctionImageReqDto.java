package com.example.auction.auction.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AuctionImageReqDto {

    private Long id; // 기존 이미지의 경우 값이 있음
    private MultipartFile file; // 새로 추가할 때 값이 있음
    private Boolean isMain;
    private Integer sortOrder;
}
