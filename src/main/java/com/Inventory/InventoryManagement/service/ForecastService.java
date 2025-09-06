package com.Inventory.InventoryManagement.service;

import com.Inventory.InventoryManagement.entity.Sale;
import com.Inventory.InventoryManagement.repository.SalesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ForecastService {
    private static final Logger logger = LoggerFactory.getLogger(ForecastService.class);
    @Autowired
    private SalesRepository salesRepository;
    public ResponseEntity<?> calculateForecast(LocalDate startDate, int days)
    {
        List<Sale> sales = salesRepository.findByDateOrderByDateAsc(startDate);

        if (sales.isEmpty()) {
            logger.error("No sales data available!");
            throw new IllegalStateException("No sales data available after " + startDate);
        }

        int n = sales.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        // Treat index as time (x), sales quantity as y
        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += sales.get(i).getQuantity();
            sumXY += i * sales.get(i).getQuantity();
            sumX2 += i * i;
        }

        // Calculate slope (b) and intercept (a)
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        // Forecast future days
        List<Double> futureForecasts = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            double nextX = n + i;
            double forecast = intercept + slope * nextX;
            futureForecasts.add(forecast);
        }

        logger.info("Linear regression forecast calculated");
        return ResponseEntity.ok(futureForecasts);
    }
}