package com.example.orderservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité Order représentant une commande dans le système
 * 
 * Cette entité illustre le concept de "Database per Service" et
 * la gestion des relations dans une architecture microservices.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_customer_id", columnList = "customer_id"),
    @Index(name = "idx_order_status", columnList = "status"),
    @Index(name = "idx_order_created_at", columnList = "created_at")
})
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "L'ID du client est obligatoire")
    @Size(max = 100, message = "L'ID du client ne peut pas dépasser 100 caractères")
    @Column(name = "customer_id", nullable = false, length = 100)
    private String customerId;
    
    @NotNull(message = "Le statut de la commande est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;
    
    @NotNull(message = "Le montant total est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant total doit être supérieur à 0")
    @DecimalMax(value = "999999.99", message = "Le montant total ne peut pas dépasser 999999.99")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
    @Column(length = 500)
    private String notes;
    
    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();
    
    // Constructeurs
    public Order() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }
    
    public Order(String customerId, BigDecimal totalAmount) {
        this();
        this.customerId = customerId;
        this.totalAmount = totalAmount;
    }
    
    // Méthodes de cycle de vie JPA
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Méthodes métier
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
    
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }
    
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }
    
    public boolean isCompleted() {
        return status == OrderStatus.DELIVERED || status == OrderStatus.CANCELLED;
    }
    
    public void confirm() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Seules les commandes en attente peuvent être confirmées");
        }
        this.status = OrderStatus.CONFIRMED;
    }
    
    public void cancel(String reason) {
        if (!canBeCancelled()) {
            throw new IllegalStateException("Cette commande ne peut pas être annulée");
        }
        this.status = OrderStatus.CANCELLED;
        this.cancellationReason = reason;
    }
    
    public void markAsDelivered() {
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Seules les commandes confirmées peuvent être marquées comme livrées");
        }
        this.status = OrderStatus.DELIVERED;
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
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
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
    
    public List<OrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    
    @Override
    public String toString() {
        return "Order{" +
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
