package com.example.auction.auction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AuctionImageResDto {

    private Long id; // 생성된 DB id
    private String url; // 프론트에서 접근할 때 사용
    private Integer sortOrder;
    private Boolean isMain; // boolean 타입으로 할 경우 직렬화 시 is 접두사를 제거하여 key를 main으로 매핑함
}
