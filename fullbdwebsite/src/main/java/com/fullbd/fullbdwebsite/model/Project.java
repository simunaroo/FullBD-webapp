package com.fullbd.fullbdwebsite.model;

import jakarta.persistence.*;

@Entity
@Table(name = "projects") // Đặt tên bảng là "projects"
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;       // Tên dự án

    // --- PHẦN ĐÃ SỬA ĐỔI ---
    // Thay thế String bằng đối tượng Category
    @ManyToOne // Nhiều Project thuộc về 1 Category
    @JoinColumn(name = "category_id", nullable = false) // Tạo cột khóa ngoại 'category_id' trong DB
    private Category category;
    // -----------------------

    @Column(columnDefinition = "TEXT") // Cho phép mô tả dài hơn 255 ký tự
    private String description; // Mô tả
    
    private String client;      // Chủ đầu tư
    private String image;       // Đường dẫn ảnh
    private String status;      // Hoàn thành/Đang làm

    // Constructor mặc định (Bắt buộc cho JPA)
    public Project() {
    }

    // --- GETTERS VÀ SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter/Setter cho Category đã được cập nhật kiểu dữ liệu
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}