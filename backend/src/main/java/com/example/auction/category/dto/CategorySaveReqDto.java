package com.example.auction.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategorySaveReqDto {

    @NotBlank
    private String id; // 카테고리 계층 삽입 및 수정시 구분하기 위한 임시 트리노드 아이디

    @NotBlank
    private String name;
}
