package com.qualimark.ecommerce.productService.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO pour les requêtes de création/modification de produits
 * 
 * Ce DTO illustre le pattern Data Transfer Object pour la validation
 * et la sérialisation des données dans les API REST.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
public class ProductRequest {
    
    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String name;
    
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;
    
    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix doit être supérieur à 0")
    @DecimalMax(value = "999999.99", message = "Le prix ne peut pas dépasser 999999.99")
    private BigDecimal price;
    
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    @Max(value = 10000, message = "Le stock ne peut pas dépasser 10000")
    private Integer stock;
    
    @NotBlank(message = "La catégorie est obligatoire")
    @Size(max = 50, message = "La catégorie ne peut pas dépasser 50 caractères")
    private String category;
    
    @Size(max = 50, message = "Le SKU ne peut pas dépasser 50 caractères")
    private String sku;
    
    @DecimalMin(value = "0.0", message = "Le poids ne peut pas être négatif")
    private BigDecimal weight;
    
    @Size(max = 100, message = "Les dimensions ne peuvent pas dépasser 100 caractères")
    private String dimensions;
    
    // Constructeurs
    public ProductRequest() {}
    
    public ProductRequest(String name, String description, BigDecimal price, Integer stock, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }
    
    // Getters et Setters
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
    
    @Override
    public String toString() {
        return "ProductRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", category='" + category + '\'' +
                ", sku='" + sku + '\'' +
                ", weight=" + weight +
                ", dimensions='" + dimensions + '\'' +
                '}';
    }
}
