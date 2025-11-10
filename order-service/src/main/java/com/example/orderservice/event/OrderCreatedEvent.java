package com.example.orderservice.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Événement émis lors de la création d'une commande
 * 
 * Cet événement peut être consommé par d'autres services pour :
 * - Traitement du paiement
 * - Planification de la livraison
 * - Envoi de notifications
 * - Mise à jour des stocks
 * 
 * @author Formation Microservices
 * @version 1.0
 */
public class OrderCreatedEvent extends OrderEvent {
    
    private final String customerId;
    private final BigDecimal totalAmount;
    private final String status;
    
    public OrderCreatedEvent(Long orderId, String customerId, BigDecimal totalAmount, String status) {
        super(orderId, "ORDER_CREATED");
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.status = status;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public String getStatus() {
        return status;
    }
    
    @Override
    public String toString() {
        return String.format("OrderCreatedEvent{eventId='%s', orderId=%d, customerId='%s', " +
                "totalAmount=%s, status='%s', timestamp=%s}", 
                getEventId(), getOrderId(), customerId, totalAmount, status, getTimestamp());
    }
}
