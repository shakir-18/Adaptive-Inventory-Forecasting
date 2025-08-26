package com.Inventory.InventoryManagement.controller;

import com.Inventory.InventoryManagement.dto.ProductDTO;
import com.Inventory.InventoryManagement.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private AdminService service;

    @PostMapping("/addProduct")
    public ResponseEntity<?> order(@Valid @RequestBody ProductDTO productDTO)
    {
        return service.addProduct(productDTO.getName(),productDTO.getStock(),productDTO.getLeadTime(),productDTO.getThreshold());
    }
    @GetMapping("/getAllProducts")
    public ResponseEntity<?> getAllProducts()
    {
        return service.getAllProducts();
    }
    @GetMapping("/getStockUpdates")
    public ResponseEntity<?> getStockUpdates()
    {
        return service.getStockUpdates();
    }

    @DeleteMapping("/deleteProductById/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long id)
    {
        return service.deleteProductById(id);
    }
    @PostMapping("/orderProductById/{id}/{quantity}")
    public ResponseEntity<?> orderProductById(@PathVariable Long id,@PathVariable Long quantity)
    {
        return service.orderProductById(id,quantity);
    }
    @PutMapping("/receiveProductById/{id}")
    public ResponseEntity<?> receiveOrderById(@PathVariable Long id)
    {
        return service.receiveOrderById(id);
    }
    @GetMapping("/getLogs")
    public ResponseEntity<?> getLogs()
    {
        return service.getLogs();
    }
}