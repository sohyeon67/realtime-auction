package com.example.auction.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryUpdateReqDto {

    @NotBlank
    private String id; // 새로 생성된 노드를 받기 위해 Long이 아닌 String으로 받기

    @NotBlank
    private String name;

    private String parentId;

    private int priority;
}
