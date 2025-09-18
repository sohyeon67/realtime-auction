package com.example.auction.category.dto;

import com.example.auction.category.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryHierarchyResDto {

    private Long id;
    private String name;
    private int depth;
    private int priority;
    private List<CategoryHierarchyResDto> children;

    public static CategoryHierarchyResDto of(Category category) {
        return new CategoryHierarchyResDto(
                category.getId(),
                category.getName(),
                category.getDepth(),
                category.getPriority(),
                category.getChildren().stream()
                        .map(CategoryHierarchyResDto::of)
                        .collect(Collectors.toList())
        );
    }
}
