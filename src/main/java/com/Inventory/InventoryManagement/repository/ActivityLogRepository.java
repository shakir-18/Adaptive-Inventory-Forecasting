package com.Inventory.InventoryManagement.repository;

import com.Inventory.InventoryManagement.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog,Long> {

}
