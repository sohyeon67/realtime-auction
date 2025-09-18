package com.example.auction.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategorySaveReqDto {

    @NotBlank
    private String name;

    private Long parentId;
}
