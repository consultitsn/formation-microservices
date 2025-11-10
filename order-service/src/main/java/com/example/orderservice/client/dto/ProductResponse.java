package com.example.orderservice.client.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour les réponses du ProductService
 * 
 * Ce DTO illustre la communication entre services et la sérialisation
 * des données dans les appels REST.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
public class ProductResponse {
    
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String category;
    private String sku;
    private BigDecimal weight;
    private String dimensions;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
    
    // Constructeurs
    public ProductResponse() {}
    
    public ProductResponse(Long id, String name, BigDecimal price, Integer stock, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private ProductResponse productResponse = new ProductResponse();
        
        public Builder id(Long id) {
            productResponse.id = id;
            return this;
        }
        
        public Builder name(String name) {
            productResponse.name = name;
            return this;
        }
        
        public Builder description(String description) {
            productResponse.description = description;
            return this;
        }
        
        public Builder price(BigDecimal price) {
            productResponse.price = price;
            return this;
        }
        
        public Builder stock(Integer stock) {
            productResponse.stock = stock;
            return this;
        }
        
        public Builder category(String category) {
            productResponse.category = category;
            return this;
        }
        
        public Builder sku(String sku) {
            productResponse.sku = sku;
            return this;
        }
        
        public Builder weight(BigDecimal weight) {
            productResponse.weight = weight;
            return this;
        }
        
        public Builder dimensions(String dimensions) {
            productResponse.dimensions = dimensions;
            return this;
        }
        
        public Builder isActive(Boolean isActive) {
            productResponse.isActive = isActive;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            productResponse.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(LocalDateTime updatedAt) {
            productResponse.updatedAt = updatedAt;
            return this;
        }
        
        public Builder version(Long version) {
            productResponse.version = version;
            return this;
        }
        
        public ProductResponse build() {
            return productResponse;
        }
    }
    
    // Méthodes utilitaires
    public boolean isAvailable() {
        return isActive != null && isActive && stock != null && stock > 0;
    }
    
    public boolean canReserve(int quantity) {
        return isAvailable() && stock >= quantity;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getStock() {
        return stock;
    }
    
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getSku() {
        return sku;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    public BigDecimal getWeight() {
        return weight;
    }
    
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    
    public String getDimensions() {
        return dimensions;
    }
    
    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
    
    @Override
    public String toString() {
        return "ProductResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", category='" + category + '\'' +
                ", sku='" + sku + '\'' +
                ", weight=" + weight +
                ", dimensions='" + dimensions + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", version=" + version +
                '}';
    }
}
