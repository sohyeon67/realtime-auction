package com.example.auction.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryUpdateResDto {

    private Long id;
    private String name;
    private Long parentId;
}
