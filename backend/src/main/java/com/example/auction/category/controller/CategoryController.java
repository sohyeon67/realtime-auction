package com.example.auction.category.controller;

import com.example.auction.category.dto.CategoryBatchReqDto;
import com.example.auction.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // 단일 요청으로 한 번에 처리
    @PostMapping("/batch")
    public ResponseEntity<?> saveCategories(@RequestBody @Valid CategoryBatchReqDto dto) {
        categoryService.saveBatch(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getCategoryList() {
        return ResponseEntity.ok(categoryService.getCategoryHierarchy());
    }
}
