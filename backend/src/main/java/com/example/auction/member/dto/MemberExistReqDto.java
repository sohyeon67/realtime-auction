package com.example.auction.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberExistReqDto {

    @NotBlank
    private String username;
}
