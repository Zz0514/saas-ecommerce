package com.example.ecommerce.dto;

import lombok.*;

/**
 * 登录 / 注册 的请求体：前端传来的用户名和密码。
 * 注意：这是「对外传输对象」，与数据库实体 User 分开，避免直接暴露实体字段。
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthRequest {
    private String username;
    private String password;
}
