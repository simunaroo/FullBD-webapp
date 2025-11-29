package com.fullbd.fullbdwebsite.repository;

import com.fullbd.fullbdwebsite.model.AiUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AiUsageLogRepository extends JpaRepository<AiUsageLog, Long> {
    // 1. Đếm số lượng (cho thống kê tổng)
    long countByUsedAtBetween(LocalDateTime start, LocalDateTime end);

    // 2. Lấy danh sách chi tiết (để phân tích theo giờ)
    List<AiUsageLog> findAllByUsedAtBetween(LocalDateTime start, LocalDateTime end);
}