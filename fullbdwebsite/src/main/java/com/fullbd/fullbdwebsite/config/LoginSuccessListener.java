package com.fullbd.fullbdwebsite.config;

import com.fullbd.fullbdwebsite.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private LogService logService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        // Lấy thông tin người vừa đăng nhập
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();

            // Ghi log (Màu xanh dương - text-primary)
            logService.saveLog("Đăng nhập", "Người dùng <b>" + username + "</b> đã đăng nhập hệ thống.",
                    "text-primary");
        }
    }
}