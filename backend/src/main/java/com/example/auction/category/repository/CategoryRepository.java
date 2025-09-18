package com.example.auction.category.repository;

import com.example.auction.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    public List<Category> findByParentIdIsNullOrderByPriorityAsc();
}
