package com.example.ecommerce.repository;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 购物车数据访问层。
 */
public interface CartRepository extends JpaRepository<Cart, Long> {
    // 按用户主键查购物车（一个用户对应一个购物车）
    Optional<Cart> findByUserId(Long userId);
}
