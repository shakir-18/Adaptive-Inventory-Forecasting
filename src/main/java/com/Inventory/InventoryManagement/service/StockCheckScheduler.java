package com.Inventory.InventoryManagement.service;

import com.Inventory.InventoryManagement.entity.Product;
import com.Inventory.InventoryManagement.entity.ProductStockUpdate;
import com.Inventory.InventoryManagement.repository.ProductRepository;
import com.Inventory.InventoryManagement.repository.ProductStockUpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class StockCheckScheduler {

    private static final Logger logger = LoggerFactory.getLogger(StockCheckScheduler.class);
    @Autowired
    private ProductStockUpdateRepository stockUpdateRepository;
    @Autowired
    private ProductRepository productRepository;
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkStockLevels() {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            if (product.getStock() < product.getThreshold()) {
                logger.info("scheduled order creation started");
                ProductStockUpdate update = new ProductStockUpdate();
                update.setProductId(product.getId());
                update.setStock(product.getStock());
                update.setThreshold(product.getThreshold());
                update.setMessage("Stock is below threshold for product ID: " + product.getId());
                stockUpdateRepository.save(update);
            }
        }
        logger.info("scheduled order creation ended");
    }
}
