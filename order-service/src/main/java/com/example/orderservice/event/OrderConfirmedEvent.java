package com.example.orderservice.event;

import java.math.BigDecimal;

/**
 * Événement déclenché lorsqu'une commande est confirmée
 * 
 * @author Formation Microservices
 * @version 1.0
 */
public class OrderConfirmedEvent extends OrderEvent {
    
    private final String customerId;
    private final BigDecimal totalAmount;
    
    public OrderConfirmedEvent(Long orderId, String customerId, BigDecimal totalAmount) {
        super(orderId, "ORDER_CONFIRMED");
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
        return "OrderConfirmedEvent{" +
                "orderId=" + getOrderId() +
                ", customerId='" + customerId + '\'' +
                ", totalAmount=" + totalAmount +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}

