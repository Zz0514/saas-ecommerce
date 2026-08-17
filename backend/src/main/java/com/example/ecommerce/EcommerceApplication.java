package com.example.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类：运行 main 方法即启动整个 Spring Boot 应用。
 * @SpringBootApplication 已包含组件扫描、自动配置、EnableJpa 等能力。
 */
@SpringBootApplication
public class EcommerceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
}
