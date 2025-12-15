package com.fullbd.fullbdwebsite.controller;

import com.fullbd.fullbdwebsite.model.Project;
import com.fullbd.fullbdwebsite.model.ProjectImage;
import com.fullbd.fullbdwebsite.repository.ProjectImageRepository;
import com.fullbd.fullbdwebsite.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;

@Controller
@RequestMapping("/admin/projects")
public class ProjectImageController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectImageRepository projectImageRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    // 1. Hiển thị trang quản lý ảnh của 1 dự án
    @GetMapping("/{id}/gallery")
    public String showGallery(@PathVariable Long id, Model model) {
        Project project = projectRepository.findById(id).orElseThrow();
        List<ProjectImage> images = projectImageRepository.findByProjectId(id);

        model.addAttribute("project", project);
        model.addAttribute("images", images);
        return "admin/project-gallery"; // Trả về file HTML mới
    }

    // 2. Xử lý Upload nhiều ảnh
    @PostMapping("/{id}/gallery/upload")
    public String uploadImages(@PathVariable Long id,
            @RequestParam("files") MultipartFile[] files,
            RedirectAttributes redirectAttributes) {
        Project project = projectRepository.findById(id).orElseThrow();

        // Tạo thư mục uploads nếu chưa có
        Path uploadPath = Paths.get(uploadDir);
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            for (MultipartFile file : files) {
                if (file.isEmpty())
                    continue;

                // Lưu file vào thư mục
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                }

                // Lưu đường dẫn vào Database
                ProjectImage image = new ProjectImage();
                image.setProject(project);
                image.setImageUrl("/project-images/" + fileName); // Đường dẫn ảo đã cấu hình
                projectImageRepository.save(image);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Đã tải lên " + files.length + " ảnh.");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi tải ảnh: " + e.getMessage());
        }

        return "redirect:/admin/projects/" + id + "/gallery";
    }

    // 3. Xóa ảnh
    @GetMapping("/gallery/delete/{imageId}")
    public String deleteImage(@PathVariable Long imageId) {
        ProjectImage image = projectImageRepository.findById(imageId).orElseThrow();
        Long projectId = image.getProject().getId();

        // Xóa file vật lý (Tùy chọn, ở đây mình chỉ xóa DB cho đơn giản)
        // ... code xóa file ...

        projectImageRepository.delete(image);
        return "redirect:/admin/projects/" + projectId + "/gallery";
    }
}