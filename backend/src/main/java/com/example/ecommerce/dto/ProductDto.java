package com.example.ecommerce.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * 商品的新增 / 修改 请求体。
 * categoryId 是对分类表的主键引用（只传 ID，不传整个分类对象），
 * 在 Service 里再查成真正的 Category 实体。
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductDto {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private Long categoryId;
    private Boolean active;
}
