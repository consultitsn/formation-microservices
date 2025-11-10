package com.example.orderservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Entité OrderItem représentant un article dans une commande
 * 
 * Cette entité illustre la gestion des relations dans une architecture
 * microservices et la référence aux entités d'autres services.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_item_order_id", columnList = "order_id"),
    @Index(name = "idx_order_item_product_id", columnList = "product_id")
})
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @NotNull(message = "L'ID du produit est obligatoire")
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(max = 200, message = "Le nom du produit ne peut pas dépasser 200 caractères")
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;
    
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    @Max(value = 1000, message = "La quantité ne peut pas dépasser 1000")
    @Column(nullable = false)
    private Integer quantity;
    
    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix unitaire doit être supérieur à 0")
    @DecimalMax(value = "999999.99", message = "Le prix unitaire ne peut pas dépasser 999999.99")
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    @NotNull(message = "Le prix total est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix total doit être supérieur à 0")
    @DecimalMax(value = "999999.99", message = "Le prix total ne peut pas dépasser 999999.99")
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
    
    @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
    @Column(length = 500)
    private String notes;
    
    // Constructeurs
    public OrderItem() {}
    
    public OrderItem(Long productId, String productName, Integer quantity, BigDecimal unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    // Méthodes métier
    public void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
    
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity < 1) {
            throw new IllegalArgumentException("La quantité doit être au moins 1");
        }
        this.quantity = newQuantity;
        calculateTotalPrice();
    }
    
    public void updateUnitPrice(BigDecimal newUnitPrice) {
        if (newUnitPrice == null || newUnitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le prix unitaire doit être supérieur à 0");
        }
        this.unitPrice = newUnitPrice;
        calculateTotalPrice();
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateTotalPrice();
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                ", notes='" + notes + '\'' +
                '}';
    }
}
