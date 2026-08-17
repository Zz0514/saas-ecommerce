package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * 购物车：与用户一对一绑定（一个用户一个购物车）。items 为购物车内的商品行。
 */
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;
}
