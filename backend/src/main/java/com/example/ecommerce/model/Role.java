package com.example.ecommerce.model;

/**
 * 角色枚举：决定用户能访问哪些接口。
 * CUSTOMER 普通顾客 / ADMIN 后台管理员 / MERCHANT 商家（多租户阶段会用到）。
 */
public enum Role {
    CUSTOMER, ADMIN, MERCHANT
}
