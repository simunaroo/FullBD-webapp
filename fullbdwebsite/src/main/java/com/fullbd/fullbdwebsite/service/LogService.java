package com.fullbd.fullbdwebsite.service;

import com.fullbd.fullbdwebsite.model.ActivityLog;
import com.fullbd.fullbdwebsite.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService {

    @Autowired
    private ActivityLogRepository logRepository;

    public void saveLog(String action, String content, String color) {
        ActivityLog log = new ActivityLog(action, content, color);
        logRepository.save(log);
    }

    public List<ActivityLog> getRecentActivities() {
        return logRepository.findTop10ByOrderByTimeDesc();
    }
}