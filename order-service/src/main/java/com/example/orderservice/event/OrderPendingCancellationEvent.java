package com.example.orderservice.event;

/**
 * Événement déclenché lorsqu'une commande est en attente d'annulation
 * 
 * @author Formation Microservices
 * @version 1.0
 */
public class OrderPendingCancellationEvent extends OrderEvent {
    
    private final String customerId;
    private final String cancellationReason;
    
    public OrderPendingCancellationEvent(Long orderId, String customerId, String cancellationReason) {
        super(orderId, "ORDER_PENDING_CANCELLATION");
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
        return "OrderPendingCancellationEvent{" +
                "orderId=" + getOrderId() +
                ", customerId='" + customerId + '\'' +
                ", cancellationReason='" + cancellationReason + '\'' +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}

