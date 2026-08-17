package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * 商品分类表：用于对商品做归类（如「数码」「服饰」）。
 */
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;
}
