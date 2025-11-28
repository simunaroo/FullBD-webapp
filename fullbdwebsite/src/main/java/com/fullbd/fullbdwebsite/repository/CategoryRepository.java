package com.fullbd.fullbdwebsite.repository;

import com.fullbd.fullbdwebsite.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Spring Data JPA tự động cung cấp các hàm findAll, save, delete, findById...
    // Bạn không cần viết gì thêm trừ khi muốn tìm kiếm đặc biệt.
}