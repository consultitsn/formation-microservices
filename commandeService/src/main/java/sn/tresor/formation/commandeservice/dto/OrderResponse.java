package sn.tresor.formation.commandeservice.dto;

import sn.tresor.formation.commandeservice.model.Commande;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO pour les réponses de commandes
 * 
 * Ce DTO illustre la sérialisation des entités pour les réponses REST.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
public class OrderResponse {
    
    private Long id;
    private String customerId;
    private String status;
    private BigDecimal totalAmount;
    private String notes;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
    private List<OrderItemResponse> items;
    
    // Constructeurs
    public OrderResponse() {}
    
    public OrderResponse(Commande order) {
        this.id = order.getId();
        this.customerId = order.getCustomerId();
        this.status = order.getStatus();
        this.totalAmount = order.getTotalAmount();
        this.createdAt = order.getCreationDate();
        this.updatedAt = order.getLastUpdate();
        this.items = order.getDetailsCommandes() != null ?
            order.getDetailsCommandes().stream()
                .map(OrderItemResponse::new)
                .collect(Collectors.toList()) : null;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getCancellationReason() {
        return cancellationReason;
    }
    
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Long getVersion() {
        return version;
    }
    
    public void setVersion(Long version) {
        this.version = version;
    }
    
    public List<OrderItemResponse> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }
    
    @Override
    public String toString() {
        return "OrderResponse{" +
                "id=" + id +
                ", customerId='" + customerId + '\'' +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", notes='" + notes + '\'' +
                ", cancellationReason='" + cancellationReason + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", version=" + version +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                '}';
    }
}

