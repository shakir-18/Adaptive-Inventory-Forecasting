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
        double alpha=0.7;
        List<Sale> sales=salesRepository.findByDateOrderByDateAsc(startDate);
        if(sales.isEmpty())
        {
            logger.error("No sales data available!");
            throw new IllegalStateException("No sales data available after "+startDate);
        }
        double forecast=sales.get(0).getQuantity();
        for(int i=1;i<sales.size();i++)
        {
            forecast=alpha*sales.get(i-1).getQuantity()+(1-alpha)*forecast;
        }
        List<Double> futureForecasts = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            forecast = alpha * sales.get(sales.size() - 1).getQuantity() + (1 - alpha) * forecast;
            futureForecasts.add(forecast);
        }
        logger.info("Forecast calculated");
        return ResponseEntity.ok(futureForecasts);
    }
}