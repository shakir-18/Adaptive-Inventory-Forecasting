package com.Inventory.InventoryManagement.repository;

import com.Inventory.InventoryManagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {

}