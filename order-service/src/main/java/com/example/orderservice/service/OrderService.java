package com.example.orderservice.service;

import com.example.orderservice.client.ProductServiceClient;
import com.example.orderservice.client.dto.ProductResponse;
import com.example.orderservice.dto.OrderItemRequest;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.event.OrderEventPublisher;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.exception.ProductNotAvailableException;
import com.example.orderservice.exception.ServiceUnavailableException;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderItem;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Service pour la gestion des commandes avec communication inter-services
 * 
 * Ce service illustre toutes les bonnes pratiques pour la communication
 * entre microservices :
 * - Circuit Breaker avec Resilience4j
 * - Retry et Timeout
 * - Fallback en cas de panne
 * - Métriques et monitoring
 * - Gestion des transactions distribuées
 * 
 * @author Formation Microservices
 * @version 1.0
 */
@Service
@Transactional
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    private final ProductServiceClient productServiceClient;
    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final Counter orderCreatedCounter;
    private final Counter orderCancelledCounter;
    private final Counter orderCompletedCounter;
    private final Timer orderCreationTimer;
    private final Timer orderCancellationTimer;
    
    @Autowired
    public OrderService(ProductServiceClient productServiceClient,
                       OrderRepository orderRepository,
                       OrderEventPublisher eventPublisher,
                       MeterRegistry meterRegistry) {
        this.productServiceClient = productServiceClient;
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        
        // Initialisation des métriques
        this.orderCreatedCounter = Counter.builder("orders.created")
                .description("Number of orders created")
                .register(meterRegistry);
        
        this.orderCancelledCounter = Counter.builder("orders.cancelled")
                .description("Number of orders cancelled")
                .register(meterRegistry);
        
        this.orderCompletedCounter = Counter.builder("orders.completed")
                .description("Number of orders completed")
                .register(meterRegistry);
        
        this.orderCreationTimer = Timer.builder("orders.creation.time")
                .description("Time taken to create an order")
                .register(meterRegistry);
        
        this.orderCancellationTimer = Timer.builder("orders.cancellation.time")
                .description("Time taken to cancel an order")
                .register(meterRegistry);
    }
    
    /**
     * Récupère toutes les commandes avec pagination et cache
     * 
     * @param pageable Paramètres de pagination
     * @return Page de toutes les commandes
     */
    @Transactional(readOnly = true)
    public Page<Order> getAllOrders(Pageable pageable) {
        logger.debug("Fetching all orders with pagination: {}", pageable);
        return orderRepository.findAll(pageable);
    }
    
    /**
     * Récupère une commande par son ID avec cache
     * 
     * @param id L'ID de la commande
     * @return La commande s'elle existe
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "orders", key = "#id")
    public Optional<Order> getOrderById(Long id) {
        logger.debug("Fetching order by ID: {}", id);
        return orderRepository.findById(id);
    }
    
    /**
     * Récupère les commandes d'un client avec cache
     * 
     * @param customerId L'ID du client
     * @param pageable Paramètres de pagination
     * @return Page des commandes du client
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "customer-orders", key = "#customerId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Order> getOrdersByCustomerId(String customerId, Pageable pageable) {
        logger.debug("Fetching orders for customer: {}", customerId);
        return orderRepository.findByCustomerId(customerId, pageable);
    }
    
    /**
     * Crée une nouvelle commande avec communication vers ProductService
     * 
     * @param orderRequest Les données de la commande
     * @return La commande créée
     */
    @CircuitBreaker(name = "productService", fallbackMethod = "createOrderFallback")
    @Retry(name = "productService")
    @TimeLimiter(name = "productService")
    @CacheEvict(value = "customer-orders", allEntries = true)
    public CompletableFuture<Order> createOrder(OrderRequest orderRequest) {
        logger.info("Creating new order for customer: {}", orderRequest.getCustomerId());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
            return orderCreationTimer.recordCallable(() -> {
                // Vérifier la disponibilité des produits
                for (OrderItemRequest item : orderRequest.getItems()) {
                    Boolean available = productServiceClient.checkAvailability(item.getProductId());
                    if (!available) {
                        throw new ProductNotAvailableException(
                            "Product " + item.getProductId() + " is not available");
                    }
                }
                
                // Récupérer les informations des produits et calculer le total
                BigDecimal totalAmount = BigDecimal.ZERO;
                for (OrderItemRequest item : orderRequest.getItems()) {
                    ProductResponse product = productServiceClient.getProduct(item.getProductId());
                    logger.info("Produit recuperer: {}", product.toString());
                    BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    totalAmount = totalAmount.add(itemTotal);
                }
                
                // Créer la commande
                logger.info("Total amount: {}", totalAmount);
                Order order = new Order(orderRequest.getCustomerId(), totalAmount);
                order.setNotes(orderRequest.getNotes());
                
                // Ajouter les articles
                for (OrderItemRequest item : orderRequest.getItems()) {
                    ProductResponse product = productServiceClient.getProduct(item.getProductId());
                    logger.info("Produit a ajouter: {}", product.toString());
                    OrderItem orderItem = new OrderItem(
                        item.getProductId(),
                        product.getName(),
                        item.getQuantity(),
                        product.getPrice()
                    );
                    orderItem.setNotes(item.getNotes());
                    order.addItem(orderItem);
                }
                
                // Sauvegarder la commande
                logger.info("Before Order saved: {}", order.toString());
                Order savedOrder = orderRepository.save(order);
                logger.info("after Order saved: {}", savedOrder.toString());
                // Réserver le stock
                for (OrderItem orderItem : savedOrder.getItems()) {
                    productServiceClient.reserveStock(orderItem.getProductId(), orderItem.getQuantity());
                }
                
                // Publier l'événement
                // eventPublisher.publishOrderCreated(savedOrder);
                
                // Incrémenter le compteur de métriques
                orderCreatedCounter.increment();
                
                logger.info("Order created successfully with ID: {}", savedOrder.getId());
                return savedOrder;
            });
            } catch (Exception e) {
                e.printStackTrace();
                //logger.error("Error creating order", e);
                throw new RuntimeException(e);
            }
        });
    }
    
    /**
     * Fallback pour la création de commande en cas de panne du ProductService
     * 
     * @param orderRequest Les données de la commande
     * @param ex L'exception qui a déclenché le fallback
     * @return La commande créée en mode dégradé
     */
    public CompletableFuture<Order> createOrderFallback(OrderRequest orderRequest, Exception ex) {
        logger.error("Fallback: Cannot create order due to product service unavailability", ex);
        
        return CompletableFuture.supplyAsync(() -> {
            // Créer une commande en attente sans vérification de stock
            Order pendingOrder = new Order(orderRequest.getCustomerId(), BigDecimal.ZERO);
            pendingOrder.setStatus(OrderStatus.PENDING);
            pendingOrder.setNotes("Order created in fallback mode - " + orderRequest.getNotes());
            
            // Ajouter les articles sans vérification
            for (OrderItemRequest item : orderRequest.getItems()) {
                OrderItem orderItem = new OrderItem(
                    item.getProductId(),
                    "Product " + item.getProductId(), // Nom par défaut
                    item.getQuantity(),
                    BigDecimal.ZERO // Prix par défaut
                );
                orderItem.setNotes("Price to be determined - " + item.getNotes());
                pendingOrder.addItem(orderItem);
            }
            
            Order savedOrder = orderRepository.save(pendingOrder);
            
            // Publier un événement pour traitement ultérieur
            eventPublisher.publishOrderPending(savedOrder);
            
            logger.warn("Order created in fallback mode with ID: {}", savedOrder.getId());
            return savedOrder;
        });
    }
    
    /**
     * Annule une commande avec libération du stock
     * 
     * @param orderId L'ID de la commande
     * @param reason La raison de l'annulation
     */
    @CircuitBreaker(name = "productService", fallbackMethod = "cancelOrderFallback")
    @CacheEvict(value = {"orders", "customer-orders"}, allEntries = true)
    public void cancelOrder(Long orderId, String reason) {
        logger.info("Cancelling order: {} with reason: {}", orderId, reason);
        
        try {
        orderCancellationTimer.recordCallable(() -> {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
            
            if (!order.canBeCancelled()) {
                throw new IllegalStateException("Order cannot be cancelled in current status: " + order.getStatus());
            }
            
            // Libérer le stock réservé
            for (OrderItem item : order.getItems()) {
                try {
                    productServiceClient.releaseStock(item.getProductId(), item.getQuantity());
                } catch (Exception e) {
                    logger.warn("Failed to release stock for product {}: {}", 
                        item.getProductId(), e.getMessage());
                }
            }
            
            // Marquer la commande comme annulée
            order.cancel(reason);
            orderRepository.save(order);
            
            // Publier l'événement
            eventPublisher.publishOrderCancelled(order);
            
            // Incrémenter le compteur de métriques
            orderCancelledCounter.increment();
            
            logger.info("Order cancelled successfully: {}", orderId);
            return null;
        });
        } catch (Exception e) {
            logger.error("Error cancelling order", e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Fallback pour l'annulation de commande en cas de panne du ProductService
     * 
     * @param orderId L'ID de la commande
     * @param reason La raison de l'annulation
     * @param ex L'exception qui a déclenché le fallback
     */
    public void cancelOrderFallback(Long orderId, String reason, Exception ex) {
        logger.error("Fallback: Cannot cancel order {} due to product service unavailability", orderId, ex);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        
        // Marquer la commande pour annulation ultérieure
        order.setStatus(OrderStatus.PENDING_CANCELLATION);
        order.setCancellationReason("Pending cancellation - " + reason);
        orderRepository.save(order);
        
        // Publier un événement pour traitement ultérieur
        eventPublisher.publishOrderPendingCancellation(order);
        
        logger.warn("Order marked for pending cancellation: {}", orderId);
    }
    
    /**
     * Confirme une commande
     * 
     * @param orderId L'ID de la commande
     */
    @CacheEvict(value = {"orders", "customer-orders"}, allEntries = true)
    public void confirmOrder(Long orderId) {
        logger.info("Confirming order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        
        if (!order.getStatus().canBeConfirmed()) {
            throw new IllegalStateException("Order cannot be confirmed in current status: " + order.getStatus());
        }
        
        order.confirm();
        orderRepository.save(order);
        
        // Publier l'événement
        eventPublisher.publishOrderConfirmed(order);
        
        logger.info("Order confirmed successfully: {}", orderId);
    }
    
    /**
     * Marque une commande comme livrée
     * 
     * @param orderId L'ID de la commande
     */
    @CacheEvict(value = {"orders", "customer-orders"}, allEntries = true)
    public void markOrderAsDelivered(Long orderId) {
        logger.info("Marking order as delivered: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        
        if (!order.getStatus().canBeDelivered()) {
            throw new IllegalStateException("Order cannot be marked as delivered in current status: " + order.getStatus());
        }
        
        order.markAsDelivered();
        orderRepository.save(order);
        
        // Publier l'événement
        eventPublisher.publishOrderDelivered(order);
        
        // Incrémenter le compteur de métriques
        orderCompletedCounter.increment();
        
        logger.info("Order marked as delivered successfully: {}", orderId);
    }
    
    /**
     * Récupère les commandes par statut
     * 
     * @param status Le statut des commandes
     * @param pageable Paramètres de pagination
     * @return Page des commandes avec le statut spécifié
     */
    @Transactional(readOnly = true)
    public Page<Order> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        logger.debug("Fetching orders by status: {}", status);
        return orderRepository.findByStatus(status, pageable);
    }
    
    /**
     * Récupère les statistiques des commandes
     * 
     * @return Les statistiques des commandes
     */
    @Transactional(readOnly = true)
    public OrderStatistics getOrderStatistics() {
        logger.debug("Fetching order statistics");
        
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long confirmedOrders = orderRepository.countByStatus(OrderStatus.CONFIRMED);
        long deliveredOrders = orderRepository.countByStatus(OrderStatus.DELIVERED);
        long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);
        
        return new OrderStatistics(totalOrders, pendingOrders, confirmedOrders, deliveredOrders, cancelledOrders);
    }
}
