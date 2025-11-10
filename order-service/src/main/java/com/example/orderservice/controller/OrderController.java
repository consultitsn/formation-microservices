package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderItemResponse;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.service.OrderStatistics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * Contrôleur REST pour la gestion des commandes
 * 
 * Ce contrôleur expose les endpoints REST pour le OrderService.
 * Il illustre les bonnes pratiques de conception d'API REST avec
 * pagination, validation, gestion d'erreurs, et communication inter-services.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Management", description = "API pour la gestion des commandes")
public class OrderController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    
    private final OrderService orderService;
    
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    /**
     * Crée une nouvelle commande
     */
    @PostMapping
    @Operation(summary = "Crée une nouvelle commande", description = "Crée une nouvelle commande avec vérification de disponibilité des produits")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Commande créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides ou produit non disponible"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<OrderResponse> createOrder(
            @Parameter(description = "Données de la commande") @Valid @RequestBody OrderRequest orderRequest) {
        
        logger.info("Creating new order for customer: {}", orderRequest.getCustomerId());
        
        try {
            CompletableFuture<Order> orderFuture = orderService.createOrder(orderRequest);
            Order order = orderFuture.join(); // Attendre la complétion
            OrderResponse response = new OrderResponse(order);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error creating order", e);
            throw e; // Laisser Spring gérer l'exception
        }
    }
    
    /**
     * Récupère toutes les commandes avec pagination
     */
    @GetMapping
    @Operation(summary = "Récupère toutes les commandes", description = "Retourne la liste paginée de toutes les commandes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des commandes récupérée avec succès"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @Parameter(description = "Numéro de page (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Critère de tri") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Direction du tri (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Order> orders = orderService.getAllOrders(pageable);
        Page<OrderResponse> response = orders.map(OrderResponse::new);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Récupère une commande par son ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupère une commande par ID", description = "Retourne une commande spécifique par son identifiant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commande trouvée"),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "ID de la commande") @PathVariable Long id) {
        
        logger.debug("Fetching order by ID: {}", id);
        
        return orderService.getOrderById(id)
                .map(order -> ResponseEntity.ok(new OrderResponse(order)))
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }
    
    /**
     * Récupère les commandes d'un client
     */
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Récupère les commandes d'un client", description = "Retourne la liste paginée des commandes d'un client spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des commandes récupérée avec succès"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Page<OrderResponse>> getOrdersByCustomerId(
            @Parameter(description = "ID du client") @PathVariable String customerId,
            @Parameter(description = "Numéro de page (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("Fetching orders for customer: {}", customerId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orders = orderService.getOrdersByCustomerId(customerId, pageable);
        Page<OrderResponse> response = orders.map(OrderResponse::new);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Récupère les commandes par statut
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Récupère les commandes par statut", description = "Retourne la liste paginée des commandes avec un statut spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des commandes récupérée avec succès"),
        @ApiResponse(responseCode = "400", description = "Statut invalide"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Page<OrderResponse>> getOrdersByStatus(
            @Parameter(description = "Statut de la commande") @PathVariable String status,
            @Parameter(description = "Numéro de page (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size) {
        
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            logger.debug("Fetching orders with status: {}", orderStatus);
            
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Order> orders = orderService.getOrdersByStatus(orderStatus, pageable);
            Page<OrderResponse> response = orders.map(OrderResponse::new);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid order status: {}", status);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Confirme une commande
     */
    @PutMapping("/{id}/confirm")
    @Operation(summary = "Confirme une commande", description = "Confirme une commande en attente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commande confirmée avec succès"),
        @ApiResponse(responseCode = "400", description = "La commande ne peut pas être confirmée"),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<OrderResponse> confirmOrder(
            @Parameter(description = "ID de la commande") @PathVariable Long id) {
        
        logger.info("Confirming order: {}", id);
        
        orderService.confirmOrder(id);
        return orderService.getOrderById(id)
                .map(order -> ResponseEntity.ok(new OrderResponse(order)))
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }
    
    /**
     * Annule une commande
     */
    @PutMapping("/{id}/cancel")
    @Operation(summary = "Annule une commande", description = "Annule une commande avec une raison")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commande annulée avec succès"),
        @ApiResponse(responseCode = "400", description = "La commande ne peut pas être annulée"),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<OrderResponse> cancelOrder(
            @Parameter(description = "ID de la commande") @PathVariable Long id,
            @Parameter(description = "Raison de l'annulation") @RequestParam(required = false, defaultValue = "Cancelled by user") String reason) {
        
        logger.info("Cancelling order: {} with reason: {}", id, reason);
        
        orderService.cancelOrder(id, reason);
        return orderService.getOrderById(id)
                .map(order -> ResponseEntity.ok(new OrderResponse(order)))
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }
    
    /**
     * Marque une commande comme livrée
     */
    @PutMapping("/{id}/deliver")
    @Operation(summary = "Marque une commande comme livrée", description = "Marque une commande confirmée comme livrée")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commande marquée comme livrée avec succès"),
        @ApiResponse(responseCode = "400", description = "La commande ne peut pas être marquée comme livrée"),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<OrderResponse> markOrderAsDelivered(
            @Parameter(description = "ID de la commande") @PathVariable Long id) {
        
        logger.info("Marking order as delivered: {}", id);
        
        orderService.markOrderAsDelivered(id);
        return orderService.getOrderById(id)
                .map(order -> ResponseEntity.ok(new OrderResponse(order)))
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }
    
    /**
     * Récupère les statistiques des commandes
     */
    @GetMapping("/statistics")
    @Operation(summary = "Récupère les statistiques des commandes", description = "Retourne les statistiques globales des commandes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<OrderStatistics> getOrderStatistics() {
        
        logger.debug("Fetching order statistics");
        OrderStatistics statistics = orderService.getOrderStatistics();
        
        return ResponseEntity.ok(statistics);
    }
}

