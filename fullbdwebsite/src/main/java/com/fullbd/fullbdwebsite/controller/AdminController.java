package com.fullbd.fullbdwebsite.controller;

import com.fullbd.fullbdwebsite.model.Category;
import com.fullbd.fullbdwebsite.model.Project;
import com.fullbd.fullbdwebsite.model.QuoteRequest;
import com.fullbd.fullbdwebsite.model.User;
import com.fullbd.fullbdwebsite.repository.CategoryRepository;
import com.fullbd.fullbdwebsite.repository.QuoteRequestRepository;
import com.fullbd.fullbdwebsite.repository.UserRepository;
import com.fullbd.fullbdwebsite.service.CategoryService;
import com.fullbd.fullbdwebsite.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private QuoteRequestRepository quoteRequestRepository;

    // 1. DASHBOARD & PAGES

    @GetMapping("")
    public String dashboard() {
        return "admin/index";
    }

    @GetMapping("/charts-chartjs")
    public String chartsChartjs() {
        return "admin/charts-chartjs";
    }

    // Forms
    @GetMapping("/forms-editors")
    public String formsEditors() {
        return "admin/forms-editors";
    }

    @GetMapping("/forms-elements")
    public String formsElements() {
        return "admin/forms-elements";
    }

    @GetMapping("/forms-layouts")
    public String formsLayouts() {
        return "admin/forms-layouts";
    }

    @GetMapping("/forms-validation")
    public String formsValidation() {
        return "admin/forms-validation";
    }

    // Pages (Trang phụ)
    @GetMapping("/pages-contact")
    public String pagesContact() {
        return "admin/pages-contact";
    }

    @GetMapping("/pages-error-404")
    public String pagesError404() {
        return "admin/pages-error-404";
    }

    @GetMapping("/pages-login")
    public String pagesLogin() {
        return "admin/pages-login";
    }

    @GetMapping("/pages-register")
    public String pagesRegister() {
        return "admin/pages-register";
    }

    

    // 2. QUẢN LÝ DỰ ÁN (PROJECTS)

    @GetMapping("/tables-data")
    public String tablesData(Model model) {
        // Lấy danh sách dự án từ DB
        List<Project> projects = projectService.getAllProjects();
        // Lấy danh sách danh mục (để hiển thị trong Dropdown form thêm mới)
        List<Category> categories = categoryService.getAllCategories();

        // Gửi dữ liệu sang HTML
        model.addAttribute("projects", projects);
        model.addAttribute("categories", categories);

        // Tạo một object rỗng để hứng dữ liệu từ form thêm mới
        model.addAttribute("newProject", new Project());

        return "admin/tables-data";
    }

    @PostMapping("/projects/save")
    public String saveProject(@ModelAttribute("newProject") Project project) {
        // Lưu vào DB
        projectService.saveProject(project);
        // Load lại trang bảng dữ liệu
        return "redirect:/admin/tables-data";
    }

    @GetMapping("/projects/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
        projectService.deleteProjectById(id);
        return "redirect:/admin/tables-data";
    }

    // 3. QUẢN LÝ DANH MỤC (CATEGORIES)

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("category", new Category()); // Object cho form thêm mới
        return "admin/categories"; // Tên file HTML trong templates/admin/
    }

    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute("category") Category category) {
        categoryRepository.save(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra trước xem danh mục có tồn tại không
            if (!categoryRepository.existsById(id)) {
                redirectAttributes.addFlashAttribute("error", "Danh mục không tồn tại!");
                return "redirect:/admin/categories";
            }

            categoryRepository.deleteById(id);
            // Thông báo thành công
            redirectAttributes.addFlashAttribute("message", "Xóa danh mục thành công!");
        
        } catch (DataIntegrityViolationException e) {
            // Lỗi này xảy ra khi danh mục đang được sử dụng bởi các bảng khác (ví dụ: Projects)
            redirectAttributes.addFlashAttribute("error", "Không thể xóa! Danh mục này đang chứa các Dự án. Vui lòng xóa hoặc chuyển các dự án sang danh mục khác trước.");
        } catch (Exception e) {
            // Các lỗi khác
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi không xác định: " + e.getMessage());
        }
    
        return "redirect:/admin/categories";
    }

    @GetMapping("/quotes-data")
    public String quotesData(Model model){
        List<QuoteRequest> quotes = quoteRequestRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("quotes", quotes);
        return "admin/quotes-data";
    }

    @GetMapping("/quotes/delete/{id}")
    public String deleteQuote(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        quoteRequestRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Đã xóa yêu cầu báo giá thành công!");
        return "redirect:/admin/quotes-data";
    }

    // 4. QUẢN LÝ USER PROFILE

    @GetMapping("/users-profile")
    public String usersProfile() {
        return "admin/users-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("currentUser") User updatedUser) {
        // Lấy user hiện tại từ DB (dựa vào ID của updatedUser gửi lên)
        User existingUser = userRepository.findById(updatedUser.getId()).orElse(null);

        if (existingUser != null) {
            // Cập nhật các thông tin mới
            existingUser.setFullName(updatedUser.getFullName());
            existingUser.setAbout(updatedUser.getAbout());
            existingUser.setCompany(updatedUser.getCompany());
            existingUser.setJob(updatedUser.getJob());
            existingUser.setCountry(updatedUser.getCountry());
            existingUser.setAddress(updatedUser.getAddress());
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setTwitter(updatedUser.getTwitter());
            existingUser.setFacebook(updatedUser.getFacebook());
            existingUser.setInstagram(updatedUser.getInstagram());
            existingUser.setLinkedin(updatedUser.getLinkedin());

            // Lưu vào DB
            userRepository.save(existingUser);
        }
        return "redirect:/admin/users-profile?success";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam("password") String currentPassword,
                                 @RequestParam("newpassword") String newPassword,
                                 @RequestParam("renewpassword") String renewPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {

        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user != null) {
            // 1. Kiểm tra mật khẩu cũ có đúng không
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng!");
                return "redirect:/admin/users-profile";
            }

            // 2. Kiểm tra mật khẩu mới và nhập lại có khớp không
            if (!newPassword.equals(renewPassword)) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu nhập lại không khớp!");
                return "redirect:/admin/users-profile";
            }

            // 3. Lưu mật khẩu mới (đã mã hóa)
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu thành công!");
        }

        return "redirect:/admin/users-profile";
    }
}