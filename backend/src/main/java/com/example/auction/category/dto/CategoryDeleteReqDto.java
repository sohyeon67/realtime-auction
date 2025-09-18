package com.example.auction.category.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryDeleteReqDto {

    @NotNull
    private Long id;
}
