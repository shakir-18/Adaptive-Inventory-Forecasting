package com.Inventory.InventoryManagement.service;

import com.Inventory.InventoryManagement.entity.Product;
import com.Inventory.InventoryManagement.entity.Sale;
import com.Inventory.InventoryManagement.repository.ProductRepository;
import com.Inventory.InventoryManagement.repository.SalesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class LLMService {
    private static final Logger logger = LoggerFactory.getLogger(LLMService.class);
    @Autowired
    private SalesRepository salesRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ActivityLogService activityLogService;
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    public ResponseEntity<?> extractStructuredData(String userInput) {
        logger.info("User input received");
        RestTemplate restTemplate = new RestTemplate();

        String prompt = """
                You are a data extraction assistant. 
                Extract structured JSON from the given user message.
                    Respond ONLY with valid JSON. Do not include explanations or notes.
                    If a field is missing, use null.
                    
                Schema:
                {
                  "productId": number
                  "productName": string,
                  "quantity": number
                }
                            
                Input: "%s"
                Output:
                """.formatted(userInput);

        Map<String, Object> request = Map.of(
                "model", "mistral",
                "prompt", prompt,
                "stream", false
        );

        Map response = restTemplate.postForObject(OLLAMA_URL, request, Map.class);

        String jsonOutput = (String) response.get("response");

        try {
            Sale saleRecord = objectMapper.readValue(jsonOutput, Sale.class);
            saleRecord.setDate(LocalDate.now());
            if (saleRecord.getProductName() == null ||
                    saleRecord.getQuantity() == null ||
                    saleRecord.getProductId()==null) {
                logger.error("Parsing failed: Some fields are null ");
                throw new RuntimeException("Parsing failed: Some fields are null → " + jsonOutput);
            }
            Long productId=saleRecord.getProductId();

            Product product=productRepository.findById(productId).orElseThrow(
                    ()-> new RuntimeException("Product Id does not exist!")
            );
            if(product==null) {
                logger.error("Product Id does not exist!");
            }
            if(product.getStock()<saleRecord.getQuantity())
            {
                logger.error("Not enough stock available!");
                throw new RuntimeException("Not enough stock available");
            }
            if(!product.getName().equalsIgnoreCase(saleRecord.getProductName()))
            {
                logger.error("Product names are not same!");
                throw new RuntimeException("Product names are not same!");
            }
            product.setStock(product.getStock()-saleRecord.getQuantity());
            productRepository.save(product);
            salesRepository.save(saleRecord);
            activityLogService.logAction("SALE PARSED",userInput);
            logger.info("Sale recorded successfully");
            return ResponseEntity.ok("Sale recorded successfully with id "+saleRecord.getId());

        } catch (Exception e) {
            logger.error("Cannot parse response!");
            throw new RuntimeException("Cannot parse response: " + jsonOutput, e);
        }
    }
}
