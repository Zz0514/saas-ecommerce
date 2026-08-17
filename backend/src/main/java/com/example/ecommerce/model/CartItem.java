package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * 购物车项：购物车中的单个商品及其数量（默认 1 件）。
 */
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity = 1;
}
