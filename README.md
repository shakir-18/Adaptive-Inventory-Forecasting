# Adaptive Inventory Management System

“Smarter inventory, smarter business."
An intelligent, adaptive inventory solution that optimizes stock handling, predicts restocking needs, and gives actionable insights to businesses of all sizes.










# Project Overview

Managing inventory can be a challenge – low stock, overstock, and inefficient tracking can cost businesses millions. This system solves that by:

Automating stock updates in real-time

Predicting restocking needs using adaptive trends

Providing actionable analytics for smart decisions

Securing operations with role-based access

It’s not just inventory management—it’s inventory intelligence.

# Key Features

Product & Stock Management – Add, update, track, and monitor products efficiently

Order Management – Seamlessly handle purchase & sales orders

Intelligent Product Order Alerts – Get notified when stock falls below thresholds

Employee Roles – Manage access and responsibilities securely

Analytics Dashboard – Insights on stock turnover, sales trends, and performance

Adaptive Recommendations – Learn inventory patterns for smarter restocking

Unstructured Data Parsing - Parsing the sale data into structured json response using LLM Service that uses Mistral Model of Ollama

Scheduled Order Generations - Everyday it checks the products which have stock quantity lesser than their threshold

Role Based Access - Using JWT role based access is enabled

Redis Integration - Using redis rather than DB to reduce response time for blacklisted token checking

# Tech Stack
Layer	Technology
Backend	Java 17, Spring Boot
Database	MySQL
Cache	Redis
Security	Spring Security (Role-based)
Logging	SLF4J, Logback
Build & Tools	Maven, Postman (for testing)

# Architecture

Controllers  ->  Services  ->  Repositories  ->  Database

REST APIs     Business Logic  JPA/Hibernate  MySQL


The system follows modular design for scalability:

Entities: Product, Order, Employee, StockUpdate

DTOs: Data transfer objects for secure communication

Services: Business logic and analytics

Controllers: REST API endpoints

Repositories: JPA/Hibernate for database operations


⚙ Configuration

Set up application.properties for database & cache:

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/inventory_db
spring.datasource.username=root
spring.datasource.password=yourPassword

# Redis
spring.redis.host=localhost

spring.redis.port=6379

# Logging
logging.level.root=INFO

logging.file.name=inventory-management.log

# API Endpoints

POST    -> /register  ->  Register as an employee

POST    ->/login    -> Login

POST    ->/logout   -> Logout

POST    ->/recordSale   -> Record a sale(ADMIN AND EMPLOYEE)

POST    ->/parseSale    -> Parse unstructured Data

GET     ->/getProductById/{id}  -> Get product Details

GET     ->/viewForecast/{productId}/{startDate}/{n_days}  -> View forecast of a product for next n days considering forecast calculation from date

ADMIN   ALLOWED    END POINTS

POST    ->/addProduct   -> Add Product

GET     ->/getAllProducts   -> Get All Product details

GET     ->/getStockUpdates  -> Get Stock Updates

DELETE  ->/deleteProductById/{Id}   -> Delete Product by Id

POST    ->/orderProductById/{id}/{quantity} -> Order products that have less stock

PUT     ->/receiveProductById/{id}  -> Mark Product status as received and update current stock

GET     ->/getLogs  -> Get All Logs that happened in this system


💡 Usage

Access APIs via Postman or integrate with a frontend

Authenticate using Basic Auth or JWT

Monitor logs for real-time operations





Author

Mohammed Shakir Ahmed
