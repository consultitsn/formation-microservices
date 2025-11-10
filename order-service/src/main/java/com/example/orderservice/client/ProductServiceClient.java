package com.example.orderservice.client;

import com.example.orderservice.client.dto.ProductResponse;
import com.example.orderservice.client.fallback.ProductServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Client Feign pour la communication avec le ProductService
 * 
 * Ce client illustre la communication inter-services avec :
 * - Feign Client pour les appels REST
 * - Circuit Breaker intégré
 * - Fallback en cas de panne
 * - Sécurité OAuth2
 * 
 * @author Formation Microservices
 * @version 1.0
 */
@FeignClient(
    name = "product-service",
    fallback = ProductServiceFallback.class
)
public interface ProductServiceClient {
    
    /**
     * Récupère un produit par son ID
     * 
     * @param id L'ID du produit
     * @return Les informations du produit
     */
    @GetMapping("/api/v1/products/{id}")
    ProductResponse getProduct(@PathVariable("id") Long id);
    
    /**
     * Vérifie la disponibilité d'un produit
     * 
     * @param id L'ID du produit
     * @return true si le produit est disponible
     */
    @GetMapping("/api/v1/products/{id}/availability")
    Boolean checkAvailability(@PathVariable("id") Long id);
    
    /**
     * Vérifie si un produit peut être réservé
     * 
     * @param id L'ID du produit
     * @param quantity La quantité à réserver
     * @return true si le produit peut être réservé
     */
    @GetMapping("/api/v1/products/{id}/can-reserve")
    Boolean canReserve(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);
    
    /**
     * Réserve du stock pour un produit
     * 
     * @param id L'ID du produit
     * @param quantity La quantité à réserver
     * @return Les informations du produit mis à jour
     */
    @PostMapping("/api/v1/products/{id}/reserve")
    ProductResponse reserveStock(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);
    
    /**
     * Libère du stock réservé pour un produit
     * 
     * @param id L'ID du produit
     * @param quantity La quantité à libérer
     * @return Les informations du produit mis à jour
     */
    @PostMapping("/api/v1/products/{id}/release")
    ProductResponse releaseStock(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);
    
    /**
     * Met à jour le stock d'un produit
     * 
     * @param id L'ID du produit
     * @param stock Le nouveau stock
     * @return Les informations du produit mis à jour
     */
    @PatchMapping("/api/v1/products/{id}/stock")
    ProductResponse updateStock(@PathVariable("id") Long id, @RequestParam("stock") Integer stock);
}
