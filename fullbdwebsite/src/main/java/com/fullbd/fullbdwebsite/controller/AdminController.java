package com.fullbd.fullbdwebsite.controller;

import com.fullbd.fullbdwebsite.model.AiUsageLog;
import com.fullbd.fullbdwebsite.model.Category;
import com.fullbd.fullbdwebsite.model.Project;
import com.fullbd.fullbdwebsite.model.QuoteRequest;
import com.fullbd.fullbdwebsite.model.User;
import com.fullbd.fullbdwebsite.repository.AiUsageLogRepository;
import com.fullbd.fullbdwebsite.repository.CategoryRepository;
import com.fullbd.fullbdwebsite.repository.ProjectRepository;
import com.fullbd.fullbdwebsite.repository.QuoteRequestRepository;
import com.fullbd.fullbdwebsite.repository.UserRepository;
import com.fullbd.fullbdwebsite.service.CategoryService;
import com.fullbd.fullbdwebsite.service.ProjectService;
import com.fullbd.fullbdwebsite.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Autowired
    private AiUsageLogRepository aiUsageLogRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private LogService logService;

    // 1. DASHBOARD & PAGES

    @GetMapping("")
    public String dashboard(Model model) {
        // --- 1. XỬ LÝ DỮ LIỆU BIỂU ĐỒ AI (12 GIỜ GẦN NHẤT) ---

        // A. Chuẩn bị khung dữ liệu (Map để giữ thứ tự thời gian)
        Map<String, Long> aiStats = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:00");

        // Tạo key cho 12 tiếng gần nhất (i chạy từ 11 về 0 để xếp từ cũ nhất -> mới
        // nhất)
        // Ví dụ: Bây giờ 16h -> Map sẽ có key: 05:00, 06:00 ... 16:00
        for (int i = 11; i >= 0; i--) {
            String hourLabel = now.minusHours(i).format(hourFormatter);
            aiStats.put(hourLabel, 0L); // Khởi tạo giá trị ban đầu là 0
        }

        // B. Lấy dữ liệu thực tế từ DB (trong 12 tiếng qua)
        LocalDateTime twelveHoursAgo = now.minusHours(12);
        // Lưu ý: Cần đảm bảo Repository đã có hàm findAllByUsedAtBetween
        List<AiUsageLog> recentLogs = aiUsageLogRepository.findAllByUsedAtBetween(twelveHoursAgo, now);

        // C. Đếm số lượng vào từng khung giờ
        for (AiUsageLog log : recentLogs) {
            // Lấy giờ của log và format thành chuỗi (VD: "14:00")
            String hourLabel = log.getUsedAt().format(hourFormatter);

            // Nếu giờ này nằm trong Map 12 tiếng đã tạo thì tăng biến đếm
            if (aiStats.containsKey(hourLabel)) {
                aiStats.put(hourLabel, aiStats.get(hourLabel) + 1);
            }
        }

        // D. Tách ra 2 danh sách riêng biệt để gửi sang View (Chart.js cần 2 mảng
        // riêng)
        List<String> aiChartLabels = new ArrayList<>(aiStats.keySet()); // Trục hoành (Giờ)
        List<Long> aiChartData = new ArrayList<>(aiStats.values()); // Trục tung (Số lượng)

        model.addAttribute("aiChartLabels", aiChartLabels);
        model.addAttribute("aiChartData", aiChartData);

        // --- 2. CÁC DỮ LIỆU KHÁC ---
        model.addAttribute("activities", logService.getRecentActivities());

        return "admin/index";
    }

    @GetMapping("/charts-chartjs")
    public String chartsChartjs(Model model) {

        // --- BIỂU ĐỒ 1: DỰ ÁN THEO DANH MỤC (Pie Chart) ---
        List<Object[]> projectStats = projectRepository.countProjectsByCategory();
        List<String> catLabels = new ArrayList<>();
        List<Long> catData = new ArrayList<>();

        for (Object[] row : projectStats) {
            catLabels.add((String) row[0]); // Tên danh mục
            catData.add((Long) row[1]); // Số lượng
        }
        model.addAttribute("catLabels", catLabels);
        model.addAttribute("catData", catData);

        // --- BIỂU ĐỒ 2: SỐ LƯỢT DÙNG AI (Bar Chart - 7 ngày gần nhất) ---
        List<String> aiLabels = new ArrayList<>();
        List<Long> aiData = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime start = now.minusDays(i).truncatedTo(ChronoUnit.DAYS);
            LocalDateTime end = start.plusDays(1).minusNanos(1);

            long count = aiUsageLogRepository.countByUsedAtBetween(start, end);

            aiLabels.add(start.format(DateTimeFormatter.ofPattern("dd/MM")));
            aiData.add(count);
        }
        model.addAttribute("aiLabels", aiLabels);
        model.addAttribute("aiData", aiData);

        // --- BIỂU ĐỒ 3: YÊU CẦU BÁO GIÁ (Line Chart - Theo tháng) ---
        // A. Chuẩn bị khung dữ liệu cho 6 tháng (Map để giữ thứ tự)
        Map<String, Long> monthlyStats = new LinkedHashMap<>();
        LocalDateTime noww = LocalDateTime.now();

        // Tạo key cho 6 tháng gần nhất (VD: "Tháng 10", "Tháng 11"...) với giá trị ban
        // đầu là 0
        for (int i = 5; i >= 0; i--) {
            String monthLabel = "Tháng " + now.minusMonths(i).getMonthValue();
            monthlyStats.put(monthLabel, 0L);
        }

        // B. Lấy dữ liệu thực tế từ DB (từ 6 tháng trước đến nay)
        LocalDateTime sixMonthsAgo = noww.minusMonths(6);
        List<QuoteRequest> recentQuotes = quoteRequestRepository.findAllByCreatedAtAfter(sixMonthsAgo);

        // C. Đếm số lượng vào từng tháng
        for (QuoteRequest q : recentQuotes) {
            String monthLabel = "Tháng " + q.getCreatedAt().getMonthValue();
            if (monthlyStats.containsKey(monthLabel)) {
                monthlyStats.put(monthLabel, monthlyStats.get(monthLabel) + 1);
            }
        }

        // D. Tách ra 2 danh sách để gửi sang View
        List<String> quoteLabels = new ArrayList<>(monthlyStats.keySet());
        List<Long> quoteData = new ArrayList<>(monthlyStats.values());

        model.addAttribute("quoteLabels", quoteLabels);
        model.addAttribute("quoteData", quoteData);

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
    public String saveProject(@ModelAttribute("newProject") Project project, Principal principal) {
        // Lưu vào DB
        projectService.saveProject(project);
        // Load lại trang bảng dữ liệu

        String action = (project.getId() == null) ? "Thêm dự án" : "Cập nhật dự án";
        // Màu xanh lá (text-success)
        logService.saveLog(action, principal.getName() + " đã lưu dự án: " + project.getTitle(), "text-success");

        return "redirect:/admin/tables-data";
    }

    @GetMapping("/projects/delete/{id}")
    public String deleteProject(@PathVariable Long id, Principal principal) {
        projectService.deleteProjectById(id);

        logService.saveLog("Xóa dự án", principal.getName() + " đã xóa dự án ID: " + id, "text-danger");

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
    public String saveCategory(@ModelAttribute("category") Category category, Principal principal) {
        categoryRepository.save(category);

        logService.saveLog("Thêm danh mục", principal.getName() + " thêm danh mục: " + category.getName(),
                "text-success");

        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            // Kiểm tra trước xem danh mục có tồn tại không
            if (!categoryRepository.existsById(id)) {
                redirectAttributes.addFlashAttribute("error", "Danh mục không tồn tại!");
                return "redirect:/admin/categories";
            }

            categoryRepository.deleteById(id);

            logService.saveLog("Xóa danh mục", principal.getName() + " xóa danh mục ID: " + id, "text-danger");

            // Thông báo thành công
            redirectAttributes.addFlashAttribute("message", "Xóa danh mục thành công!");

        } catch (DataIntegrityViolationException e) {
            // Lỗi này xảy ra khi danh mục đang được sử dụng bởi các bảng khác (ví dụ:
            // Projects)
            redirectAttributes.addFlashAttribute("error",
                    "Không thể xóa! Danh mục này đang chứa các Dự án. Vui lòng xóa hoặc chuyển các dự án sang danh mục khác trước.");
        } catch (Exception e) {
            // Các lỗi khác
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi không xác định: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    @GetMapping("/quotes-data")
    public String quotesData(Model model) {
        List<QuoteRequest> quotes = quoteRequestRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("quotes", quotes);
        return "admin/quotes-data";
    }

    @GetMapping("/quotes/delete/{id}")
    public String deleteQuote(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        quoteRequestRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Đã xóa yêu cầu báo giá thành công!");
        logService.saveLog("Xóa báo giá", principal.getName() + " xóa yêu cầu báo giá ID: " + id, "text-warning");
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
            logService.saveLog("Cập nhật hồ sơ",
                    "Người dùng " + updatedUser.getFullName() + " đã cập nhật thông tin cá nhân.", "text-info");
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

    @PostMapping("/projects/import")
    public String importProjects(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        List<String> errorLogs = new ArrayList<>();
        int successCount = 0;

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên

            // Duyệt từ dòng thứ 2 (bỏ qua Header dòng 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                // 1. Đọc dữ liệu từ Excel (Giả sử thứ tự cột: Tên | Chủ đầu tư | Danh mục |
                // Trạng thái | Ảnh | Mô tả)
                String title = getCellValue(row.getCell(0));
                String client = getCellValue(row.getCell(1));
                String categoryName = getCellValue(row.getCell(2));
                String status = getCellValue(row.getCell(3));
                String image = getCellValue(row.getCell(4));
                String desc = getCellValue(row.getCell(5));

                // 2. Validate dữ liệu
                // Kiểm tra bắt buộc: Tên và Danh mục
                if (title.isEmpty() || categoryName.isEmpty()) {
                    errorLogs.add("Dòng " + (i + 1) + ": Thiếu Tên dự án hoặc Tên danh mục.");
                    continue;
                }

                // Kiểm tra trùng tên
                if (projectRepository.existsByTitle(title)) {
                    errorLogs.add("Dòng " + (i + 1) + ": Dự án '" + title + "' đã tồn tại.");
                    continue;
                }

                // Tìm danh mục trong DB
                Optional<Category> categoryOpt = categoryRepository.findByName(categoryName);
                if (categoryOpt.isEmpty()) {
                    errorLogs
                            .add("Dòng " + (i + 1) + ": Danh mục '" + categoryName + "' không tồn tại trong hệ thống.");
                    continue;
                }

                // 3. Tạo và lưu Project
                Project p = new Project();
                p.setTitle(title);
                p.setClient(client.isEmpty() ? null : client); // Để trống nếu không có
                p.setCategory(categoryOpt.get());
                p.setStatus(status.isEmpty() ? "Sắp triển khai" : status); // Mặc định nếu trống
                p.setImage(image.isEmpty() ? null : image);
                p.setDescription(desc.isEmpty() ? null : desc);

                projectService.saveProject(p);
                successCount++;
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi đọc file: " + e.getMessage());
        }

        // Gửi kết quả về View
        redirectAttributes.addFlashAttribute("importSuccessCount", successCount);
        if (!errorLogs.isEmpty()) {
            redirectAttributes.addFlashAttribute("importErrors", errorLogs);
        }

        return "redirect:/admin/tables-data";
    }

    // Hàm phụ trợ để lấy giá trị String từ ô Excel an toàn
    @SuppressWarnings("deprecation")
    private String getCellValue(Cell cell) {
        if (cell == null)
            return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}