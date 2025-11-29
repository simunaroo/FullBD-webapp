package com.fullbd.fullbdwebsite.repository;

import com.fullbd.fullbdwebsite.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    // Lấy 10 hoạt động mới nhất để hiển thị Dashboard
    List<ActivityLog> findTop10ByOrderByTimeDesc();
}