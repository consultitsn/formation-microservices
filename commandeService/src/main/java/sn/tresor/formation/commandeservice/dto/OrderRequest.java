package sn.tresor.formation.commandeservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO pour les requêtes de création de commandes
 * 
 * Ce DTO illustre la validation des données et la structure
 * des requêtes dans les API REST.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
public class OrderRequest {
    
    @NotBlank(message = "L'ID du client est obligatoire")
    @Size(max = 100, message = "L'ID du client ne peut pas dépasser 100 caractères")
    private String customerId;
    
    @NotEmpty(message = "La commande doit contenir au moins un article")
    @Valid
    private List<OrderItemRequest> items;
    ;
    
    // Constructeurs
    public OrderRequest() {}
    
    public OrderRequest(String customerId, List<OrderItemRequest> items) {
        this.customerId = customerId;
        this.items = items;
    }
    
    // Getters et Setters
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public List<OrderItemRequest> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
    

    @Override
    public String toString() {
        return "OrderRequest{" +
                "customerId='" + customerId + '\'' +
                ", items=" + items +
                '}';
    }
}
