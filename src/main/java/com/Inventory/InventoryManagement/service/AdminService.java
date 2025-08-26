package com.Inventory.InventoryManagement.service;

import com.Inventory.InventoryManagement.entity.Order;
import com.Inventory.InventoryManagement.entity.Product;
import com.Inventory.InventoryManagement.repository.OrderRepository;
import com.Inventory.InventoryManagement.repository.ProductRepository;
import com.Inventory.InventoryManagement.repository.ProductStockUpdateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;

@Service
public class AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ActivityLogService activityLogService;
    @Autowired
    private ProductStockUpdateRepository stockUpdateRepository;

    public ResponseEntity<?> addProduct(String name, Long stock, int leadTime, Long threshold)
    {
        Product product =new Product();
        product.setName(name);
        product.setStock(stock);
        product.setThreshold(threshold);
        product.setLeadTime(leadTime);
        product.setCreated(LocalDate.now());
        productRepository.save(product);
        logger.info("Created product with Id : {}",product.getId());
        activityLogService.logAction("CREATE_PRODUCT","Created product with ID: " + product.getId());
        return ResponseEntity.ok("Product Added Successfully with id "+product.getId()+" and name "+product.getName()
        +" \nUse this id further.");
    }
    public ResponseEntity<?> getAllProducts()
    {
        List<Product> products=productRepository.findAll();
        activityLogService.logAction("PRODUCTS FETCHED","Fetched all products");
        logger.info("Fetched all products");
        return ResponseEntity.ok(products);
    }
    public ResponseEntity<?> getStockUpdates()
    {
        logger.info("Fetched all Stock Updates");
        return ResponseEntity.ok(stockUpdateRepository.findAll());
    }
    public ResponseEntity<?> deleteProductById(Long id)
    {
        Product product=productRepository.findById(id).orElseThrow(()->
                new RuntimeException("Product Id not found!"));
        if(product==null)
        {
            logger.error("Product Id not found!");
        }
        productRepository.deleteById(id);
        activityLogService.logAction("DELETE_PRODUCT","Deleted product with ID: " + id);
        logger.info("Product Deleted Successfully");
        return ResponseEntity.ok("Product Deleted Successfully");
    }
    public ResponseEntity<?> orderProductById(Long productId,Long quantity)
    {
        Product product=productRepository.findById(productId).orElseThrow(()->
                new RuntimeException("Product id not found!"));
        Order order=new Order();
        order.setProductId(productId);
        order.setProductName(product.getName());
        order.setStatus("Pending");
        order.setQuantity(quantity);
        order.setExpectedDate(LocalDate.now().plusDays(7));
        orderRepository.save(order);
        activityLogService.logAction("ORDERED_PRODUCT","Ordered product with ID: " + productId);
        logger.info("Order Placed Successfully");
        return ResponseEntity.ok("Order Placed Successfully");
    }
    public ResponseEntity<?> receiveOrderById(Long orderId)
    {
        Order order=orderRepository.findById(orderId).orElseThrow(()->
                new RuntimeException("Order id not found!"));
        order.setStatus("Received");
        orderRepository.save(order);
        activityLogService.logAction("RECEIVED_ORDER","Received order with ID: " + orderId);
        Product product=productRepository.findById(order.getProductId()).orElseThrow(()->
                new RuntimeException("Product Id not found!"));
        if(product==null)
        {
            logger.error("Product Id not found!");
        }
        product.setStock(product.getStock()+order.getQuantity());
        product.getOrders().add(order);
        logger.info("Order Received Successfully");
        return ResponseEntity.ok("Order Received Successfully");
    }
    public ResponseEntity<?> getLogs()
    {
        logger.info("Logs fetched Successfully");
        return activityLogService.getLogs();
    }
}