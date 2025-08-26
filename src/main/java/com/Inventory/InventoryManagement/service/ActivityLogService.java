package com.Inventory.InventoryManagement.service;

import com.Inventory.InventoryManagement.entity.ActivityLog;
import com.Inventory.InventoryManagement.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ActivityLogService {
    @Autowired
    private ActivityLogRepository logRepo;
    private static final Logger logger = LoggerFactory.getLogger(ActivityLogService.class);

    public void logAction(String action,String details) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = auth != null ? auth.getName() : "SYSTEM";
        String role = auth != null && auth.getAuthorities() != null
                ? auth.getAuthorities().toString()
                : "UNKNOWN";

        ActivityLog log = new ActivityLog();
        log.setUsername(username);
        log.setRole(role);
        log.setAction(action);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());

        logRepo.save(log);
    }
    public ResponseEntity<?> getLogs()
    {
        return ResponseEntity.ok(logRepo.findAll());
    }
}
