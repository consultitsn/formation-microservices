package com.example.orderservice.repository;

import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour la gestion des commandes
 * 
 * Cette interface illustre le pattern Repository et sera utilisée
 * par le OrderService dans l'architecture microservices.
 * Elle utilise Spring Data JPA pour simplifier l'accès aux données.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Recherche des commandes par client avec pagination
     * 
     * @param customerId L'ID du client
     * @param pageable Paramètres de pagination
     * @return Page des commandes du client
     */
    Page<Order> findByCustomerId(String customerId, Pageable pageable);
    
    /**
     * Recherche des commandes par statut avec pagination
     * 
     * @param status Le statut des commandes
     * @param pageable Paramètres de pagination
     * @return Page des commandes avec le statut spécifié
     */
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    /**
     * Recherche des commandes par client et statut
     * 
     * @param customerId L'ID du client
     * @param status Le statut des commandes
     * @param pageable Paramètres de pagination
     * @return Page des commandes du client avec le statut spécifié
     */
    Page<Order> findByCustomerIdAndStatus(String customerId, OrderStatus status, Pageable pageable);
    
    /**
     * Recherche des commandes créées dans une plage de dates
     * 
     * @param startDate Date de début
     * @param endDate Date de fin
     * @param pageable Paramètres de pagination
     * @return Page des commandes créées dans la plage
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    Page<Order> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate, 
                                      Pageable pageable);
    
    /**
     * Recherche des commandes par montant total
     * 
     * @param minAmount Montant minimum
     * @param maxAmount Montant maximum
     * @param pageable Paramètres de pagination
     * @return Page des commandes dans la plage de montants
     */
    @Query("SELECT o FROM Order o WHERE o.totalAmount BETWEEN :minAmount AND :maxAmount")
    Page<Order> findByTotalAmountBetween(@Param("minAmount") Double minAmount, 
                                        @Param("maxAmount") Double maxAmount, 
                                        Pageable pageable);
    
    /**
     * Compte le nombre de commandes par statut
     * 
     * @param status Le statut
     * @return Le nombre de commandes
     */
    long countByStatus(OrderStatus status);
    
    /**
     * Compte le nombre de commandes par client
     * 
     * @param customerId L'ID du client
     * @return Le nombre de commandes
     */
    long countByCustomerId(String customerId);
    
    /**
     * Recherche des commandes en attente d'annulation
     * 
     * @return Liste des commandes en attente d'annulation
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * Recherche des commandes créées récemment
     * 
     * @param hours Nombre d'heures
     * @return Liste des commandes créées dans les dernières heures
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt >= :cutoffTime")
    List<Order> findRecentOrders(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Recherche des commandes avec des articles spécifiques
     * 
     * @param productId L'ID du produit
     * @param pageable Paramètres de pagination
     * @return Page des commandes contenant le produit
     */
    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.productId = :productId")
    Page<Order> findByProductId(@Param("productId") Long productId, Pageable pageable);
    
    /**
     * Recherche des commandes par client avec tri par date de création
     * 
     * @param customerId L'ID du client
     * @param pageable Paramètres de pagination avec tri
     * @return Page des commandes du client triées
     */
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId ORDER BY o.createdAt DESC")
    Page<Order> findByCustomerIdOrderByCreatedAtDesc(@Param("customerId") String customerId, Pageable pageable);
    
    /**
     * Recherche des commandes actives (non terminées)
     * 
     * @param pageable Paramètres de pagination
     * @return Page des commandes actives
     */
    @Query("SELECT o FROM Order o WHERE o.status IN ('PENDING', 'CONFIRMED', 'PREPARING', 'READY_FOR_DELIVERY', 'IN_DELIVERY')")
    Page<Order> findActiveOrders(Pageable pageable);
    
    /**
     * Recherche des commandes terminées
     * 
     * @param pageable Paramètres de pagination
     * @return Page des commandes terminées
     */
    @Query("SELECT o FROM Order o WHERE o.status IN ('DELIVERED', 'CANCELLED', 'FAILED')")
    Page<Order> findCompletedOrders(Pageable pageable);
}
