package com.Inventory.InventoryManagement.repository;

import com.Inventory.InventoryManagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
