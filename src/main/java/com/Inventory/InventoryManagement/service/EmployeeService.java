package com.Inventory.InventoryManagement.service;

import com.Inventory.InventoryManagement.entity.Employee;
import com.Inventory.InventoryManagement.entity.Product;
import com.Inventory.InventoryManagement.entity.ProductStockUpdate;
import com.Inventory.InventoryManagement.entity.Sale;
import com.Inventory.InventoryManagement.repository.EmployeeRepository;
import com.Inventory.InventoryManagement.repository.ProductStockUpdateRepository;
import com.Inventory.InventoryManagement.repository.SalesRepository;
import com.Inventory.InventoryManagement.repository.ProductRepository;
import com.Inventory.InventoryManagement.utility.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SalesRepository salesRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomDetailsService customDetailsService;
    @Autowired
    private TokenBlacklistService blacklistService;
    @Autowired
    private ForecastService forecastService;
    @Autowired
    private ActivityLogService activityLogService;
    @Autowired
    private LLMService llmService;
    @Autowired
    private ProductStockUpdateRepository stockUpdateRepository;
    public ResponseEntity<?> register(String name,String password)
    {
        Employee employee =new Employee();
        employee.setName(name);
        employee.setPassword(passwordEncoder.encode(password));
        employee.setRole("EMPLOYEE");
        employeeRepository.save(employee);
        logger.info("Registered Employee with Id {}",employee.getId());
        activityLogService.logAction("REGISTERED_EMPLOYEE","Registered Employee with Id "+employee.getId());
        return ResponseEntity.ok("Employee Registered Successfully with Id "+employee.getId());
    }
    public ResponseEntity<?> login(Long id, String password)
    {
        Employee employee=employeeRepository.findById(id).orElseThrow(()-> new RuntimeException("Employee/Admin with id "+ id+" does not exist"));
        if(passwordEncoder.matches(password,employee.getPassword()))
        {
            UserDetails userDetails=customDetailsService.loadUserByUsername(id.toString());
            String token= jwtUtil.generateToken(userDetails);
            logger.info("Employee logged in with Id {}",employee.getId());
            activityLogService.logAction("LOGGED_IN_EMPLOYEE","Employee Login with Id "+employee.getId());
            return ResponseEntity.ok(token);
        }
        logger.error("Invalid Credentials!");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials!");
    }
    public ResponseEntity<?> logout(HttpServletRequest request)
    {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authorities: " + auth.getAuthorities());
            try {
                String userId = jwtUtil.extractUserId(token);
                logger.info("Employee Logged out with Id " + userId);
                activityLogService.logAction("LOGOUT_EMPLOYEE",
                        "Employee Logged out with Id " + userId);
                long expiry = jwtUtil.getRemainingValidity(token);
                blacklistService.blacklist(token, expiry);
                SecurityContextHolder.clearContext();
            } catch (Exception e) {
                logger.error("Invalid or expired token");
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                        .body("Invalid or expired token");
            }
        }
        return ResponseEntity.ok("Logged out Successfully.");
    }
    public ResponseEntity<?> recordSale(Long productId, Long quantity)
    {
        Product product=productRepository.findById(productId).orElse(null);
        if(product!=null) {
            if(product.getStock()<quantity)
            {
                logger.error("Not Enough Stock available!");
                throw new RuntimeException("Not Enough Stock available!");
            }
            Sale sale = new Sale();
            sale.setProductId(productId);
            sale.setProductName(product.getName());
            sale.setQuantity(quantity);
            sale.setDate(LocalDate.now());
            salesRepository.save(sale);
            activityLogService.logAction("SALE_RECORDED","Employee Recorded sale with Id "+sale.getId()+" and quantity "+sale.getQuantity()+" of product with id "+sale.getProductId());
            product.setStock(product.getStock()-quantity);
            product.getSales().add(sale);
            productRepository.save(product);
            if(product.getStock()<product.getThreshold())
            {
                ProductStockUpdate productStockUpdate = new ProductStockUpdate();
                productStockUpdate.setProductId(productId);
                productStockUpdate.setThreshold(product.getThreshold());
                productStockUpdate.setStock(product.getStock());
                productStockUpdate.setMessage("Stock is below threshold for product ID: " + product.getId());
                stockUpdateRepository.save(productStockUpdate);
            }
            logger.info("Sale recorded successfully");
            return ResponseEntity.ok("Sale recorded successfully.");
        }
        logger.error("Product Not Found!");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found!");
    }
    public ResponseEntity<?> parseSale(String data)
    {
        return llmService.extractStructuredData(data);
    }
    public ResponseEntity<?> getProductById(Long productId)
    {
        Product product=productRepository.findById(productId).orElseThrow(()->
                new RuntimeException("Product id"+ productId+"does not exist"));
        if(product==null)
        {
            logger.error("Product id"+ productId+"does not exist");
        }
        ResponseEntity<?> response=viewForecast(productId,LocalDate.now().minusDays(7),1);
        activityLogService.logAction("PRODUCT_FETCHED","Product with Id "+productId);
        logger.info("Product fetched with Id : {}",productId);
        return ResponseEntity.ok(product.toString()+" \n\n"+response.toString());
    }
    public ResponseEntity<?> viewForecast(Long productId, LocalDate startDate, int days)
    {
        Product product=productRepository.findById(productId).orElseThrow(()->
                new RuntimeException("Product with id "+productId+" does not exist!"));
        if(product==null)
        {
            logger.error("Product id"+ productId+"does not exist");
        }
        logger.info("Product forecasted with Id : {}",productId);
        activityLogService.logAction("PRODUCT_FORECAST","Product with Id "+productId+" was forecasted from date "+startDate+" for "+days+" days");
        return forecastService.calculateForecast(startDate,days);
    }
}