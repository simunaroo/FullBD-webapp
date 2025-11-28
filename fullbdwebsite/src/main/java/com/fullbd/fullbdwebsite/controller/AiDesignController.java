package com.fullbd.fullbdwebsite.controller;

import com.fullbd.fullbdwebsite.dto.AiDesignRequest;
import com.fullbd.fullbdwebsite.dto.AiDesignResult;
import com.fullbd.fullbdwebsite.service.AiEstimationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AiDesignController {

    @Autowired
    private AiEstimationService aiEstimationService;

    @GetMapping("/ai-design")
    public String showForm(Model model) {
        model.addAttribute("request", new AiDesignRequest());
        return "ai-design";
    }

    @PostMapping("/ai-design/generate")
    public String generateResult(@ModelAttribute("request") AiDesignRequest request, Model model) {
        AiDesignResult result = aiEstimationService.calculate(request);
        model.addAttribute("result", result);
        model.addAttribute("request", request); // Gửi lại request để hiển thị lại form nếu cần
        return "ai-design"; // Trả về cùng 1 trang nhưng có thêm dữ liệu result
    }
}