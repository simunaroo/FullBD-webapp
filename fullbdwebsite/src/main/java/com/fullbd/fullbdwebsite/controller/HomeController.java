package com.fullbd.fullbdwebsite.controller;

import com.fullbd.fullbdwebsite.model.Category;
import com.fullbd.fullbdwebsite.model.Project;
import com.fullbd.fullbdwebsite.model.QuoteRequest;
import com.fullbd.fullbdwebsite.repository.QuoteRequestRepository;
import com.fullbd.fullbdwebsite.service.ProjectService;
import com.fullbd.fullbdwebsite.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private QuoteRequestRepository quoteRequestRepository; // Thêm Repository để lưu báo giá

    // 1. Trang chủ
    @GetMapping(value = {"/", "/index"})
    public String home(Model model) {
        List<Project> projects = projectService.getAllProjects();
        // Cắt lấy 6 dự án đầu tiên để hiển thị trang chủ
        if(projects.size() > 6) {
            projects = projects.subList(0, 6);
        }
        model.addAttribute("projects", projects);
        return "index";
    }

    // 2. Các trang tĩnh
    @GetMapping("/404")
    public String p404(){
        return "404";
    }

    @GetMapping("/about")
    public String about(){
        return "about";
    }

    @GetMapping("/contact")
    public String contact(){
        return "contact";
    }

    @GetMapping("/privacy")
    public String privacy(){
        return "privacy";
    }

    @GetMapping("/service-details")
    public String servicedetails(){
        return "service-details";
    }

    @GetMapping("/services")
    public String services(){
        return "services";
    }

    @GetMapping("/starter-page")
    public String starterpage(){
        return "starter-page";
    }

    @GetMapping("/team")
    public String team(){
        return "team";
    }

    @GetMapping("/terms")
    public String terms(){
        return "terms";
    }

    // 3. Quản lý hiển thị Dự án (Public)
    @GetMapping("/projects")
    public String projects(Model model) {
        List<Project> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        return "projects";
    }

    @GetMapping("/project-details")
    public String projectDetails(@RequestParam("id") Long id, Model model) {
        Optional<Project> project = projectService.getProjectById(id);
        
        if (project.isPresent()) {
            model.addAttribute("project", project.get());
            return "project-details";
        } else {
            return "redirect:/projects";
        }
    }

    // 4. Xử lý Báo giá (Gộp từ QuoteController sang)
    
    @GetMapping("/quote")
    public String quote(Model model){
        // 1. Lấy danh sách từ DB
        List<Category> categories = categoryService.getAllCategories();
        
        // DEBUG: In ra console để kiểm tra xem có lấy được dữ liệu không
        System.out.println("Số lượng danh mục lấy được: " + categories.size());

        // 2. Gửi sang View
        model.addAttribute("categories", categories); 
        model.addAttribute("quoteRequest", new QuoteRequest());
        
        return "quote";
    }

    // Xử lý submit form báo giá
    @PostMapping("/quote/submit")
    public String submitQuote(@ModelAttribute("quoteRequest") QuoteRequest quoteRequest, RedirectAttributes redirectAttributes) {
        try {
            // Lưu vào Database
            quoteRequestRepository.save(quoteRequest);
            
            // Thông báo thành công
            redirectAttributes.addFlashAttribute("message", "Yêu cầu báo giá của bạn đã được gửi thành công! Chúng tôi sẽ liên hệ sớm nhất.");
        } catch (Exception e) {
            // Thông báo lỗi
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, vui lòng thử lại sau!");
        }
        
        return "redirect:/quote"; // Quay lại trang báo giá
    }
}