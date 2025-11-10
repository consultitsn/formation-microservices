package sn.tresor.formation.commandeservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

    // Constructeurs
    public OrderItemRequest() {}
    
    public OrderItemRequest(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
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
    
    @Override
    public String toString() {
        return "OrderItemRequest{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }
}
