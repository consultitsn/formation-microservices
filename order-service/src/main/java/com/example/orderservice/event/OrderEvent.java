package com.example.orderservice.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Classe de base pour tous les événements liés aux commandes
 * 
 * Cette classe illustre le pattern Event Sourcing et la communication
 * asynchrone entre services dans une architecture microservices.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
public abstract class OrderEvent {
    
    private final String eventId;
    private final Long orderId;
    private final Instant timestamp;
    private final String eventType;
    
    protected OrderEvent(Long orderId, String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.eventType = eventType;
        this.timestamp = Instant.now();
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    @Override
    public String toString() {
        return String.format("%s{eventId='%s', orderId=%d, timestamp=%s}", 
                getClass().getSimpleName(), eventId, orderId, timestamp);
    }
}
