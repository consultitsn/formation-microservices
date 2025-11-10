package com.example.orderservice.event;

import java.math.BigDecimal;

/**
 * Événement déclenché lorsqu'une commande est en attente
 * 
 * @author Formation Microservices
 * @version 1.0
 */
public class OrderPendingEvent extends OrderEvent {
    
    private final String customerId;
    private final BigDecimal totalAmount;
    
    public OrderPendingEvent(Long orderId, String customerId, BigDecimal totalAmount) {
        super(orderId, "ORDER_PENDING");
        this.customerId = customerId;
        this.totalAmount = totalAmount;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    @Override
    public String toString() {
        return "OrderPendingEvent{" +
                "orderId=" + getOrderId() +
                ", customerId='" + customerId + '\'' +
                ", totalAmount=" + totalAmount +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}

