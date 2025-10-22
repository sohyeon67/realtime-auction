package com.example.auction.bid.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidReqDto {

    @NotNull(message = "입찰 금액은 필수입니다.")
    private Long bidPrice;
}
