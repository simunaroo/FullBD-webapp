package com.fullbd.fullbdwebsite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                // 1. Cho phép truy cập tự do vào các trang tĩnh và trang khách
                .requestMatchers("/", "/index", "/about", "/services", "/projects", "/service-details", "/project-details", "/contact", "/quote", "/team", "/terms", "/privacy", "/404", "/quote/submit", "/ai-design", "/ai-design/generate").permitAll()
                .requestMatchers("/assets/**", "/admin-assets/**").permitAll() // Cho phép load CSS/JS/Ảnh
                .requestMatchers("/register", "/save-user").permitAll() // Cho phép trang đăng ký
                // 2. Các trang bắt đầu bằng /admin yêu cầu phải đăng nhập
                .requestMatchers("/admin/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin((form) -> form
                .loginPage("/login") // Đường dẫn đến trang login của mình
                .defaultSuccessUrl("/admin", true) // Đăng nhập thành công thì chuyển về dashboard
                .permitAll()
            )
            .logout((logout) -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    // Bean mã hóa mật khẩu (BCrypt là chuẩn an toàn hiện nay)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}