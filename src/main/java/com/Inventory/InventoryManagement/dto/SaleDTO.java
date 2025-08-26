package com.Inventory.InventoryManagement.dto;

import jakarta.validation.constraints.NotBlank;
public class SaleDTO {
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    @NotBlank(message = "Product Id cannot be empty!")
    private Long productId;
    @NotBlank(message = "Quantity cannot be empty!")
    private Long quantity;
}