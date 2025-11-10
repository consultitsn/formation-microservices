package com.example.orderservice.event;

/**
 * Événement déclenché lorsqu'une commande est annulée
 * 
 * @author Formation Microservices
 * @version 1.0
 */
public class OrderCancelledEvent extends OrderEvent {
    
    private final String customerId;
    private final String cancellationReason;
    
    public OrderCancelledEvent(Long orderId, String customerId, String cancellationReason) {
        super(orderId, "ORDER_CANCELLED");
        this.customerId = customerId;
        this.cancellationReason = cancellationReason;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public String getCancellationReason() {
        return cancellationReason;
    }
    
    @Override
    public String toString() {
        return "OrderCancelledEvent{" +
                "orderId=" + getOrderId() +
                ", customerId='" + customerId + '\'' +
                ", cancellationReason='" + cancellationReason + '\'' +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}

