package com.example.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour gérer les fallbacks en cas d'échec des services
 * 
 * Ce contrôleur est appelé lorsque le Circuit Breaker détecte une panne
 * d'un microservice et active le fallback.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {
    
    /**
     * Fallback pour le Product Service
     */
    @GetMapping("/product-service")
    public ResponseEntity<Map<String, Object>> productServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "Service Unavailable");
        response.put("message", "Product Service is currently unavailable. Please try again later.");
        response.put("path", "/api/v1/products");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    /**
     * Fallback pour l'Order Service
     */
    @GetMapping("/order-service")
    public ResponseEntity<Map<String, Object>> orderServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "Service Unavailable");
        response.put("message", "Order Service is currently unavailable. Please try again later.");
        response.put("path", "/api/v1/orders");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
