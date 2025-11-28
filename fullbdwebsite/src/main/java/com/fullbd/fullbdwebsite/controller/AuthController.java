package com.fullbd.fullbdwebsite.controller;

import com.fullbd.fullbdwebsite.model.User;
import com.fullbd.fullbdwebsite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Hiển thị trang đăng nhập
    @GetMapping("/login")
    public String login() {
        return "admin/pages-login"; // Trỏ đến file html login của bạn
    }

    // Hiển thị trang đăng ký
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "admin/pages-register";
    }

    // Xử lý lưu user mới
    @PostMapping("/save-user")
    public String saveUser(@ModelAttribute("user") User user) {
        // Mã hóa mật khẩu trước khi lưu
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ADMIN"); // Mặc định là ADMIN
        userRepository.save(user);
        return "redirect:/login?success";
    }
}