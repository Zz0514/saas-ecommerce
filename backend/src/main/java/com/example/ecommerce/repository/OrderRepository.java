package com.example.ecommerce.repository;

import com.example.ecommerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 订单数据访问层。
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
}
