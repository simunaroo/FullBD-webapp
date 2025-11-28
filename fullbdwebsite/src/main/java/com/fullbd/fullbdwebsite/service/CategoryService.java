package com.fullbd.fullbdwebsite.service;

import com.fullbd.fullbdwebsite.model.Category;
import com.fullbd.fullbdwebsite.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Lấy tất cả danh mục
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Lưu danh mục
    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }

    // Xóa danh mục theo ID
    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }

    // Lấy danh mục theo ID
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
}