package com.Inventory.InventoryManagement.repository;

import com.Inventory.InventoryManagement.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface SalesRepository extends JpaRepository<Sale,Long> {
    List<Sale> findByDateOrderByDateAsc(LocalDate date);
}
