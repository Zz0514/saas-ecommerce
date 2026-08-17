package com.example.ecommerce.repository;

import com.example.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 分类数据访问层。
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
