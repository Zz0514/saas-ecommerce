package com.example.ecommerce.dto;

import lombok.*;

/**
 * 登录 / 注册 的响应体：返回 JWT（token）、用户名、角色。
 * 前端拿到 token 后存到 localStorage，之后每次请求带上它。
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private String username;
    private String role;
}
