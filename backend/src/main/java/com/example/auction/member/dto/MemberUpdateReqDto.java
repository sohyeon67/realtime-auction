package com.example.auction.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdateReqDto {

    @NotBlank
    private String username;

    @NotBlank
    private String name;

    @NotBlank
    private String nickname;

    @NotBlank
    private String phone;
}
