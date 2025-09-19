package com.example.auction.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberSaveReqDto {

    @Email(message = "올바른 이메일 형식이 아닙니다")
    @NotBlank(message = "이메일을 입력하세요")
    private String username;

    @NotBlank(message = "비밀번호를 입력하세요")
    private String password;

    @NotBlank(message = "이름을 입력하세요")
    private String name;

    @NotBlank(message = "닉네임을 입력하세요")
    private String nickname;

    @NotBlank(message = "연락처를 입력하세요")
    private String phone;

}
