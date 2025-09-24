package com.example.auction.category.service;

import com.example.auction.category.domain.Category;
import com.example.auction.category.dto.*;
import com.example.auction.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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

        // (임시 ID, 실제 DB ID) 매핑
        Map<String, Long> tempIdMap = new HashMap<>();

        // 카테고리 추가 - 항상 임시 아이디가 들어온다.
        // 새로 생성된 노드끼리 부모-자식 관계가 순서대로 들어있지 않을 경우 에러가나기 때문에 저장과 수정을 분리한다.
        // 기존에는 저장할 때 parentId를 받도록 했었는데 핵심은 임시 ID를 먼저 등록하고, 부모가 있는 경우 연결하는 순서를 보장하도록 한다.

        // 새로 추가된 노드 모두 DB에 저장 (부모는 나중에 연결), 부모 지정 제거

        // 1. 새 카테고리 추가
        for (CategorySaveReqDto add : dto.getAdded()) {
            Category category = Category.builder()
                    .name(add.getName())
                    .build();

            categoryRepository.save(category); // IDENTITY 전략으로 즉시 insert, 영속성 컨텍스트에 저장

            // 임시 ID, 실제 DB ID 매핑
            tempIdMap.put(add.getId(), category.getId());
        }

        // 2. 트리 구조 기반 업데이트
        // 부모 노드와 priority 반영
        // dirty checking 이용, commit 시점에 update 쿼리 발생
        for (CategoryUpdateReqDto upd : dto.getUpdated()) {

            Long actualId;
            try {
                actualId = Long.parseLong(upd.getId()); // 기존 노드
            } catch (NumberFormatException e) {
                actualId = tempIdMap.get(upd.getId()); // 새로 생성된 노드
            }

            Category category = getCategory(actualId); // updated에 담긴 기존/새로 생성된 모든 카테고리를 조회하여 영속성 컨텍스트에서 관리
            category.changeName(upd.getName());
            category.changePriority(upd.getPriority());

            // 부모 변경
            if (upd.getParentId() != null) {
                Long actualParentId;
                try {
                    actualParentId = Long.parseLong(upd.getParentId()); // 기존 노드
                } catch (NumberFormatException e) {
                    actualParentId = tempIdMap.get(upd.getParentId()); // 새로 생성된 노드
                }
                Category newParent = getCategory(actualParentId);
                category.changeParent(newParent);
            } else {
                category.changeParent(null); // 최상위로 이동
            }
        }

        // 3. 삭제 처리
        for (CategoryDeleteReqDto del : dto.getDeleted()) {
            categoryRepository.deleteById(del.getId()); // 영속성 컨텍스트에서 객체 제거, 트랜잭션 commit 시점에 삭제 쿼리가 나간다.
        }

    }

    public List<CategoryHierarchyResDto> getCategoryHierarchy() {
        List<Category> byParentIdIsNull = categoryRepository.findByParentIdIsNullOrderByPriorityAsc();
        List<CategoryHierarchyResDto> collect = byParentIdIsNull.stream().map(CategoryHierarchyResDto::of).collect(Collectors.toList());
        return collect;
    }
}
