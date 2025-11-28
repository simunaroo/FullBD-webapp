package com.fullbd.fullbdwebsite.config;

import com.fullbd.fullbdwebsite.model.User;
import com.fullbd.fullbdwebsite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    // Hàm này sẽ chạy trước mọi request và gắn biến "currentUser" vào HTML
    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Kiểm tra nếu người dùng đã đăng nhập (và không phải là user ẩn danh)
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            // Tìm user trong DB dựa vào username đang đăng nhập
            return userRepository.findByUsername(username).orElse(null);
        }
        return null;
    }
}