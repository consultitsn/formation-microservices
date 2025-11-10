package com.example.orderservice.event;

import java.math.BigDecimal;

/**
 * Événement déclenché lorsqu'une commande est livrée
 * 
 * @author Formation Microservices
 * @version 1.0
 */
public class OrderDeliveredEvent extends OrderEvent {
    
    private final String customerId;
    private final BigDecimal totalAmount;
    
    public OrderDeliveredEvent(Long orderId, String customerId, BigDecimal totalAmount) {
        super(orderId, "ORDER_DELIVERED");
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
        return "OrderDeliveredEvent{" +
                "orderId=" + getOrderId() +
                ", customerId='" + customerId + '\'' +
                ", totalAmount=" + totalAmount +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}

