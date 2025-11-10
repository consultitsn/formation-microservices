package com.example.orderservice.client.fallback;

import com.example.orderservice.client.ProductServiceClient;
import com.example.orderservice.client.dto.ProductResponse;
import com.example.orderservice.exception.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implémentation de fallback pour le ProductServiceClient
 * 
 * Cette classe illustre le pattern Circuit Breaker et la gestion
 * des pannes de service dans une architecture microservices.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
@Component
public class ProductServiceFallback implements ProductServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceFallback.class);
    
    @Override
    public ProductResponse getProduct(Long id) {
        logger.warn("Fallback: Product service unavailable for product {}", id);
        return ProductResponse.builder()
                .id(id)
                .name("Product Unavailable")
                .description("Product service is currently unavailable")
                .price(java.math.BigDecimal.ZERO)
                .stock(0)
                .category("Unknown")
                .isActive(false)
                .build();
    }
    
    @Override
    public Boolean checkAvailability(Long id) {
        logger.warn("Fallback: Cannot check availability for product {}", id);
        return false; // Par défaut, considérer comme non disponible
    }
    
    @Override
    public Boolean canReserve(Long id, Integer quantity) {
        logger.warn("Fallback: Cannot check reservation capability for product {}", id);
        return false; // Par défaut, ne pas permettre la réservation
    }
    
    @Override
    public ProductResponse reserveStock(Long id, Integer quantity) {
        logger.warn("Fallback: Cannot reserve stock for product {} (quantity: {})", id, quantity);
        throw new ServiceUnavailableException("Product service unavailable - cannot reserve stock");
    }
    
    @Override
    public ProductResponse releaseStock(Long id, Integer quantity) {
        logger.warn("Fallback: Cannot release stock for product {} (quantity: {})", id, quantity);
        throw new ServiceUnavailableException("Product service unavailable - cannot release stock");
    }
    
    @Override
    public ProductResponse updateStock(Long id, Integer stock) {
        logger.warn("Fallback: Cannot update stock for product {} (stock: {})", id, stock);
        throw new ServiceUnavailableException("Product service unavailable - cannot update stock");
    }
}
