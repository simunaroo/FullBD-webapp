package com.fullbd.fullbdwebsite.model;

import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action; // Ví dụ: "Đăng nhập", "Thêm dự án"
    private String content; // Chi tiết: "Admin đã thêm dự án A"
    private LocalDateTime time; // Thời gian thực hiện

    // Màu sắc cho icon (success, danger, primary...) để hiển thị đẹp
    private String badgeColor;

    // Constructor, Getter, Setter
    public ActivityLog() {
        this.time = LocalDateTime.now();
    }

    public ActivityLog(String action, String content, String badgeColor) {
        this.action = action;
        this.content = content;
        this.badgeColor = badgeColor;
        this.time = LocalDateTime.now();
    }

    // Getter/Setter...
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getBadgeColor() {
        return badgeColor;
    }

    public void setBadgeColor(String badgeColor) {
        this.badgeColor = badgeColor;
    }

    // Hàm tiện ích để hiển thị "x phút trước" trên giao diện
    public String getTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(this.time, now);
        long seconds = duration.getSeconds();

        if (seconds < 60)
            return "Vừa xong";
        if (seconds < 3600)
            return (seconds / 60) + " phút trước";
        if (seconds < 86400)
            return (seconds / 3600) + " giờ trước";
        return (seconds / 86400) + " ngày trước";
    }
}