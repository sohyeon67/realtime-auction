package com.example.auction.auction.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AuctionImageReqDto {

    private MultipartFile file;
    private Boolean isMain;
    private Integer sortOrder;
}
