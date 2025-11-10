package com.example.orderservice.event;

import com.example.orderservice.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Publisher d'événements pour les commandes
 * 
 * Cette classe illustre le pattern Publisher-Subscriber et permet
 * de découpler les services en utilisant des événements asynchrones.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
@Component
public class OrderEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderEventPublisher.class);
    
    private final ApplicationEventPublisher eventPublisher;
    
    @Autowired
    public OrderEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Publie un événement de création de commande
     * 
     * @param order La commande créée
     */
    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
            order.getId(),
            order.getCustomerId(),
            order.getTotalAmount(),
            order.getStatus().name()
        );
        
        logger.info("Publishing order created event: {}", event);
        eventPublisher.publishEvent(event);
    }
    
    /**
     * Publie un événement de commande en attente
     * 
     * @param order La commande en attente
     */
    public void publishOrderPending(Order order) {
        OrderPendingEvent event = new OrderPendingEvent(
            order.getId(),
            order.getCustomerId(),
            order.getTotalAmount()
        );
        
        logger.info("Publishing order pending event: {}", event);
        eventPublisher.publishEvent(event);
    }
    
    /**
     * Publie un événement de confirmation de commande
     * 
     * @param order La commande confirmée
     */
    public void publishOrderConfirmed(Order order) {
        OrderConfirmedEvent event = new OrderConfirmedEvent(
            order.getId(),
            order.getCustomerId(),
            order.getTotalAmount()
        );
        
        logger.info("Publishing order confirmed event: {}", event);
        eventPublisher.publishEvent(event);
    }
    
    /**
     * Publie un événement d'annulation de commande
     * 
     * @param order La commande annulée
     */
    public void publishOrderCancelled(Order order) {
        OrderCancelledEvent event = new OrderCancelledEvent(
            order.getId(),
            order.getCustomerId(),
            order.getCancellationReason()
        );
        
        logger.info("Publishing order cancelled event: {}", event);
        eventPublisher.publishEvent(event);
    }
    
    /**
     * Publie un événement de commande en attente d'annulation
     * 
     * @param order La commande en attente d'annulation
     */
    public void publishOrderPendingCancellation(Order order) {
        OrderPendingCancellationEvent event = new OrderPendingCancellationEvent(
            order.getId(),
            order.getCustomerId(),
            order.getCancellationReason()
        );
        
        logger.info("Publishing order pending cancellation event: {}", event);
        eventPublisher.publishEvent(event);
    }
    
    /**
     * Publie un événement de livraison de commande
     * 
     * @param order La commande livrée
     */
    public void publishOrderDelivered(Order order) {
        OrderDeliveredEvent event = new OrderDeliveredEvent(
            order.getId(),
            order.getCustomerId(),
            order.getTotalAmount()
        );
        
        logger.info("Publishing order delivered event: {}", event);
        eventPublisher.publishEvent(event);
    }
}
