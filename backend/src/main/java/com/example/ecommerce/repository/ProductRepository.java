package com.example.ecommerce.repository;

import com.example.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 商品数据访问层：骨架阶段只用父类通用方法（查全部、按 ID 等）即可。
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
}
