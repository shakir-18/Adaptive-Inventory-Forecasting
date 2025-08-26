package com.Inventory.InventoryManagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProductDTO {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(int leadTime) {
        this.leadTime = leadTime;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public Long getThreshold() {
        return threshold;
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    @NotBlank(message = "Name cannot be empty!")
    private String name;
    @NotNull(message = "Lead time cannot be null")
    @Positive(message = "Lead time must be greater than 0")
    private int leadTime;
    @NotNull(message = "Stock cannot be empty!")
    @Positive(message = "Stock must be greater than 0")
    private Long stock;
    @NotNull(message = "Threshold cannot be empty!")
    @Positive(message = "Threshold must be greater than 0")
    private Long threshold;
}
