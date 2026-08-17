package com.example.ecommerce.repository;

import com.example.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 用户数据访问层：继承 JpaRepository 即可获得基础增删改查。
 * 下面两个方法靠「方法名派生 SQL」自动生成查询，无需手写 HQL。
 */
public interface UserRepository extends JpaRepository<User, Long> {
    // 按用户名查用户（登录时加载 Spring Security 所需的 UserDetails）
    Optional<User> findByUsername(String username);
    // 判断用户名是否存在（注册时防重名）
    boolean existsByUsername(String username);
}
