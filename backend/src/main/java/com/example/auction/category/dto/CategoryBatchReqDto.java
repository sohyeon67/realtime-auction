package com.example.auction.category.dto;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class CategoryBatchReqDto {

    @Valid
    private List<CategorySaveReqDto> added;
    @Valid
    private List<CategoryUpdateReqDto> updated;
    @Valid
    private List<CategoryDeleteReqDto> deleted;
}
