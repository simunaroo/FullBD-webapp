package com.fullbd.fullbdwebsite.service;

import com.fullbd.fullbdwebsite.model.Project;
import com.fullbd.fullbdwebsite.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    // Lấy tất cả dự án
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // Lưu dự án (Thêm mới hoặc Cập nhật)
    public void saveProject(Project project) {
        projectRepository.save(project);
    }

    // Lấy dự án theo ID (để xem chi tiết hoặc sửa)
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    // Xóa dự án
    public void deleteProjectById(Long id) {
        projectRepository.deleteById(id);
    }
}