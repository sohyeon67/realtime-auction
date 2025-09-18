package com.example.auction.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryUpdateReqDto {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

    private Long parentId;

    private int priority;
}
