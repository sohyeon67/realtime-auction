package com.example.auction.category.service;

import com.example.auction.category.domain.Category;
import com.example.auction.category.dto.*;
import com.example.auction.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    public void saveBatch(CategoryBatchReqDto dto) {

        // 새로 추가
        for (CategorySaveReqDto add : dto.getAdded()) {
            Category category = Category.builder()
                    .name(add.getName())
                    .build();
            if (add.getParentId() != null) {
                Category parent = getCategory(add.getParentId());
                category.changeParent(parent);
            }
            categoryRepository.save(category);
        }

        // 업데이트 처리
        for (CategoryUpdateReqDto upd : dto.getUpdated()) {

            Category category = getCategory(upd.getId());
            category.changeName(upd.getName());

            if (upd.getParentId() != null) {
                Category parent = getCategory(upd.getParentId());
                category.changeParent(parent);
            }
        }

        // 삭제 처리
        for (CategoryDeleteReqDto del : dto.getDeleted()) {
            categoryRepository.deleteById(del.getId());
        }

    }

    public List<CategoryHierarchyResDto> getCategoryHierarchy() {
        List<Category> byParentIdIsNull = categoryRepository.findByParentIdIsNullOrderByPriorityAsc();
        List<CategoryHierarchyResDto> collect = byParentIdIsNull.stream().map(CategoryHierarchyResDto::of).collect(Collectors.toList());
        return collect;
    }
}
