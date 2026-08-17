package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * 用户表：系统的使用者。一个用户可下多笔订单、拥有一个购物车。
 * role 区分身份：CUSTOMER（顾客）/ ADMIN（管理员）/ MERCHANT（商家）。
 */
@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.CUSTOMER;

    private String email;
    private String phone;
}
