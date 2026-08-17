package com.example.ecommerce.config;

import com.example.ecommerce.config.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 主配置。
 * - 关闭 CSRF：前后端分离 + JWT 无状态通信，不需要 CSRF 防护
 * - 关闭 Session：每次请求都带 Token，服务端不保存会话
 * - 把自定义 JwtAuthenticationFilter 放在用户名密码过滤器之前，先完成 Token 校验
 * - 通过 URL 规则区分「匿名可访问 / 需登录 / 需管理员角色」
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 允许跨域的来源，从 application.yml 的 cors.allowed-origins 读取（部署时由环境变量覆盖）
    @Value("#{'${cors.allowed-origins:http://localhost:5173,http://localhost:5174}'.split(',')}")
    private List<String> allowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 无状态：服务端不创建 HttpSession，登录态完全由前端携带的 Token 决定
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 认证/注册、商品列表与分类对所有人开放（购物无需登录也能浏览）
                .requestMatchers("/api/auth/**", "/api/products/**", "/api/categories/**").permitAll()
                // 后台管理类接口仅 ADMIN 角色可访问
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // 其余接口一律需要携带合法 Token
                .anyRequest().authenticated()
            )
            // 在用户名密码认证过滤器之前插入 JWT 校验过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
