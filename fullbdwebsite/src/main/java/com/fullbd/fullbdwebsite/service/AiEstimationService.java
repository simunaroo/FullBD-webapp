package com.fullbd.fullbdwebsite.service;

import com.fullbd.fullbdwebsite.dto.AiDesignRequest;
import com.fullbd.fullbdwebsite.dto.AiDesignResult;
import org.springframework.stereotype.Service;

@Service
public class AiEstimationService {

    public AiDesignResult calculate(AiDesignRequest request) {
        AiDesignResult result = new AiDesignResult();

        // 1. TÍNH TOÁN DIỆN TÍCH TỔNG
        double totalArea = request.getArea() * request.getFloors();

        // 2. ĐỊNH NGHĨA ĐƠN GIÁ CƠ BẢN (VNĐ/m2 - Cập nhật theo giá thị trường 2025)
        // Giá này áp dụng cho phong cách Hiện đại + Vật liệu Cơ bản
        long baseRoughPrice = 3_800_000; // Phần thô (Nhân công + Vật tư thô)
        long baseFinishPrice = 2_800_000; // Hoàn thiện (Sơn, lát sàn, thiết bị vệ sinh...)
        long baseFurniturePrice = 2_000_000; // Nội thất liền tường (Tủ bếp, tủ áo...)

        // 3. XÁC ĐỊNH HỆ SỐ PHONG CÁCH (Style Factor)
        // Phong cách càng cầu kỳ (phào chỉ, hoa văn) -> Nhân công và vật liệu hao hụt
        // càng cao
        double styleFactor = 1.0;
        switch (request.getStyle()) {
            case "indochine":
                styleFactor = 1.2; // Đông Dương: Chi tiết gỗ, gạch bông cầu kỳ hơn hiện đại
                break;
            case "neo-classic":
                styleFactor = 1.35; // Tân cổ điển: Có phào chỉ, chi tiết đắp vẽ
                break;
            case "classic":
                styleFactor = 1.6; // Cổ điển: Rất cầu kỳ, đòi hỏi thợ tay nghề cao
                break;
            case "modern":
            default:
                styleFactor = 1.0; // Hiện đại: Đơn giản, phẳng thẳng
                break;
        }

        // 4. XÁC ĐỊNH HỆ SỐ VẬT LIỆU (Material Factor)
        // Mức độ đầu tư ảnh hưởng trực tiếp đến giá vật tư hoàn thiện và nội thất
        double materialFactor = 1.0;
        switch (request.getMaterial()) {
            case "good":
                materialFactor = 1.25; // Khá: Vật liệu thương hiệu tốt (Toto, An Cường...)
                break;
            case "luxury":
                materialFactor = 1.7; // Cao cấp: Nhập khẩu, đá tự nhiên, gỗ thịt...
                break;
            case "standard":
            default:
                materialFactor = 1.0; // Cơ bản: Vật liệu phổ thông trong nước
                break;
        }

        // 5. TÍNH TOÁN CHI TIẾT TỪNG HẠNG MỤC

        // a. Phần thô (Rough Cost)
        // Phần thô ít bị ảnh hưởng bởi vật liệu (xi măng, sắt thép giá chung),
        // nhưng bị ảnh hưởng bởi phong cách (kết cấu vòm, cột phức tạp).
        // -> Ta chỉ áp dụng 30% tác động của Style lên phần thô.
        long finalRoughPrice = (long) (baseRoughPrice * (1 + (styleFactor - 1) * 0.3));
        result.setRoughCost((long) (totalArea * finalRoughPrice));

        // b. Phần hoàn thiện (Finish Cost)
        // Bị ảnh hưởng mạnh bởi cả Phong cách (nhân công) và Vật liệu (giá gạch, sơn).
        result.setFinishCost((long) (totalArea * baseFinishPrice * styleFactor * materialFactor));

        // c. Phần nội thất (Furniture Cost)
        // Tương tự hoàn thiện, phụ thuộc rất nhiều vào chất liệu gỗ và độ tinh xảo.
        result.setFurnitureCost((long) (totalArea * baseFurniturePrice * styleFactor * materialFactor));

        // 6. TỔNG CỘNG
        result.setTotalCost(result.getRoughCost() + result.getFinishCost() + result.getFurnitureCost());

        // 2. CHỌN HÌNH ẢNH THÔNG MINH
        // Xử lý số tầng: Nếu nhập > 3 tầng thì lấy ảnh mẫu 3 tầng, ngược lại lấy đúng
        // số tầng
        int floorImage = request.getFloors() > 3 ? 3 : request.getFloors();
        if (floorImage < 1)
            floorImage = 1; // Đề phòng nhập số âm

        // Lấy phong cách (mặc định là modern nếu null)
        String style = request.getStyle() != null ? request.getStyle() : "modern";

        // Tạo đường dẫn động
        String imgPath2D = "/assets/img/ai/" + style + "-" + floorImage + "-2d.webp";
        // String imgPath3D = "/assets/img/ai/" + style + "-" + floorImage + "-3d.webp";
        String imgPath3D = "/assets/img/ai/" + "classic" + "-" + "1" + "-3d.webp";

        result.setLayout2D(imgPath2D);
        result.setPerspective3D(imgPath3D);

        // 8. LỜI KHUYÊN THÔNG MINH (AI Advice)
        long budget = request.getBudget().longValue();
        long diff = result.getTotalCost() - budget;

        StringBuilder advice = new StringBuilder();
        if (diff > 0) {
            // Nếu vượt ngân sách
            advice.append("Chi phí ước tính đang cao hơn ngân sách của bạn khoảng ")
                    .append(String.format("%,d", diff))
                    .append(" VNĐ. ");

            if (request.getMaterial().equals("luxury")) {
                advice.append(
                        "Gợi ý: Bạn có thể cân nhắc chuyển vật liệu từ 'Cao cấp' xuống 'Khá' để tiết kiệm khoảng 25% chi phí hoàn thiện.");
            } else if (request.getStyle().equals("classic")) {
                advice.append(
                        "Gợi ý: Phong cách Cổ điển tốn kém chi phí nhân công đắp vẽ. Bạn có thể xem xét phong cách Tân cổ điển nhẹ nhàng hơn.");
            } else {
                advice.append(
                        "Gợi ý: Bạn có thể điều chỉnh giảm diện tích xây dựng hoặc chia giai đoạn thi công nội thất sau.");
            }
        } else {
            // Nếu đủ ngân sách
            advice.append("Ngân sách của bạn rất an toàn cho phương án này (Dư khoảng ")
                    .append(String.format("%,d", Math.abs(diff)))
                    .append(" VNĐ). ");

            if (request.getMaterial().equals("standard")) {
                advice.append(
                        "Với ngân sách này, bạn hoàn toàn có thể nâng cấp vật liệu lên mức 'Khá' để tăng độ bền và thẩm mỹ cho ngôi nhà.");
            } else {
                advice.append(
                        "Chúng tôi sẽ tập trung vào các chi tiết decor tinh tế và thiết bị thông minh (Smarthome) để nâng tầm không gian sống.");
            }
        }
        result.setAdvice(advice.toString());

        return result;
    }
}