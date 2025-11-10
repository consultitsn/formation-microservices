package com.example.orderservice.dto;

import jakarta.validation.constraints.*;

/**
 * DTO pour les articles de commande dans les requêtes
 * 
 * Ce DTO illustre la validation des données pour les articles
 * de commande dans les API REST.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
public class OrderItemRequest {
    
    @NotNull(message = "L'ID du produit est obligatoire")
    private Long productId;
    
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    @Max(value = 1000, message = "La quantité ne peut pas dépasser 1000")
    private Integer quantity;
    
    @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
    private String notes;
    
    // Constructeurs
    public OrderItemRequest() {}
    
    public OrderItemRequest(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
    
    public OrderItemRequest(Long productId, Integer quantity, String notes) {
        this.productId = productId;
        this.quantity = quantity;
        this.notes = notes;
    }
    
    // Getters et Setters
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public String toString() {
        return "OrderItemRequest{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                ", notes='" + notes + '\'' +
                '}';
    }
}
