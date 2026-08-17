package com.example.ecommerce.service;

import com.example.ecommerce.dto.AuthRequest;
import com.example.ecommerce.dto.AuthResponse;
import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证业务：注册与登录。
 * - 注册：密码用 BCrypt 加密后落库，默认角色为普通顾客 CUSTOMER
 * - 登录：交给 AuthenticationManager 校验账号密码，通过后签发 JWT
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public AuthResponse register(AuthRequest request) {
        // 防止重名注册
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        // 密码必须用 BCrypt 加密后再存库，绝不能明文
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .build();
        userRepository.save(user);
        // 注册成功直接签发 Token，省去再登录一次
        return login(request);
    }

    public AuthResponse login(AuthRequest request) {
        // AuthenticationManager 负责校验账号密码（会用到 CustomUserDetailsService + PasswordEncoder）
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        // 校验通过后，为用户生成 JWT
        String token = jwtUtil.generateToken(userDetails);
        String role = userDetails.getAuthorities().stream().findFirst()
                .map(auth -> auth.getAuthority()).orElse("");
        return AuthResponse.builder()
                .token(token)
                .username(userDetails.getUsername())
                .role(role)
                .build();
    }
}
