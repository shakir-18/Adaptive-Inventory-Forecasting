package com.Inventory.InventoryManagement.controller;

import com.Inventory.InventoryManagement.dto.EmployeeDTO;
import com.Inventory.InventoryManagement.dto.SaleDTO;
import com.Inventory.InventoryManagement.dto.SaleMessageDTO;
import com.Inventory.InventoryManagement.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
public class EmployeeController {
    @Autowired
    private EmployeeService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody EmployeeDTO employeeDTO) {
        if (employeeDTO.getName() == null || employeeDTO.getPassword() == null ||
                employeeDTO.getName().length() == 0 || employeeDTO.getPassword().length() == 0) {
            throw new RuntimeException("Name and Password cannot be empty!");
        }
        return service.register(employeeDTO.getName(), employeeDTO.getPassword());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody EmployeeDTO employeeDTO) {
        if (employeeDTO.getId() == null || employeeDTO.getPassword() == null ||
                employeeDTO.getPassword().length() == 0) {
            throw new RuntimeException("Id and Password cannot be empty!");
        }
        return service.login(employeeDTO.getId(), employeeDTO.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        return service.logout(request);
    }

    @PostMapping("/recordSale")
    public ResponseEntity<?> recordSale(@Valid @RequestBody SaleDTO saleDTO) {
        return service.recordSale(saleDTO.getProductId(), saleDTO.getQuantity());
    }
    @PostMapping("/parseSale")
    public ResponseEntity<?> parseSale(@RequestBody SaleMessageDTO saleMessageDTO)
    {
        return service.parseSale(saleMessageDTO.getData());
    }
    @GetMapping("/getProductById/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id)
    {
        return service.getProductById(id);
    }
    @GetMapping("/viewForecast/{productId}/{startDate}/{days}")
    public ResponseEntity<?> viewForecast(@PathVariable Long productId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @PathVariable int days) {
        return service.viewForecast(productId,startDate,days);
    }
}