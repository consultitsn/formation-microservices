package com.qualimark.ecommerce.productService.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour l'entité Product
 * 
 * Ces tests vérifient le comportement de l'entité, ses validations
 * et ses méthodes de cycle de vie JPA.
 */
@DisplayName("Tests unitaires Product")
class ProductTest {
    
    private Validator validator;
    private Product product;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        product = new Product(
            "Test Product",
            "Description du produit de test",
            new BigDecimal("10.00"),
            50,
            "Test"
        );
    }
    
    @Test
    @DisplayName("Devrait créer un produit avec le constructeur par défaut")
    void testDefaultConstructor() {
        // When
        Product newProduct = new Product();
        
        // Then
        assertNotNull(newProduct);
        assertNotNull(newProduct.getCreatedAt());
        assertNotNull(newProduct.getUpdatedAt());
        assertTrue(newProduct.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(newProduct.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
    
    @Test
    @DisplayName("Devrait créer un produit avec le constructeur paramétré")
    void testParameterizedConstructor() {
        // Given
        String name = "Nouveau Produit";
        String description = "Description";
        BigDecimal price = new BigDecimal("15.50");
        Integer stock = 25;
        String category = "Electronics";
        
        // When
        Product newProduct = new Product(name, description, price, stock, category);
        
        // Then
        assertNotNull(newProduct);
        assertEquals(name, newProduct.getName());
        assertEquals(description, newProduct.getDescription());
        assertEquals(price, newProduct.getPrice());
        assertEquals(stock, newProduct.getStock());
        assertEquals(category, newProduct.getCategory());
        assertNotNull(newProduct.getCreatedAt());
        assertNotNull(newProduct.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Devrait avoir des getters et setters fonctionnels")
    void testGettersAndSetters() {
        // Given
        Long id = 1L;
        String name = "Updated Name";
        String description = "Updated Description";
        BigDecimal price = new BigDecimal("25.00");
        Integer stock = 100;
        String category = "Updated Category";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        
        // When
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);
        product.setCreatedAt(createdAt);
        product.setUpdatedAt(updatedAt);
        
        // Then
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
        assertEquals(stock, product.getStock());
        assertEquals(category, product.getCategory());
        assertEquals(createdAt, product.getCreatedAt());
        assertEquals(updatedAt, product.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Devrait valider un produit valide")
    void testValidProduct() {
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertTrue(violations.isEmpty());
    }
    
    @Test
    @DisplayName("Devrait rejeter un produit avec un nom vide")
    void testInvalidName_Blank() {
        // Given
        product.setName("");
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("name") &&
                          v.getMessage().contains("obligatoire")));
    }
    
    @Test
    @DisplayName("Devrait rejeter un produit avec un nom null")
    void testInvalidName_Null() {
        // Given
        product.setName(null);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }
    
    @Test
    @DisplayName("Devrait rejeter un nom trop long")
    void testInvalidName_TooLong() {
        // Given
        String longName = "a".repeat(101); // Plus de 100 caractères
        product.setName(longName);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("name") &&
                          v.getMessage().contains("100 caractères")));
    }
    
    @Test
    @DisplayName("Devrait rejeter une description trop longue")
    void testInvalidDescription_TooLong() {
        // Given
        String longDescription = "a".repeat(501); // Plus de 500 caractères
        product.setDescription(longDescription);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("description") &&
                          v.getMessage().contains("500 caractères")));
    }
    
    @Test
    @DisplayName("Devrait accepter une description null")
    void testDescription_Null() {
        // Given
        product.setDescription(null);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        // La description est optionnelle, donc pas d'erreur
        assertTrue(violations.stream()
            .noneMatch(v -> v.getPropertyPath().toString().equals("description")));
    }
    
    @Test
    @DisplayName("Devrait rejeter un prix null")
    void testInvalidPrice_Null() {
        // Given
        product.setPrice(null);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("price") &&
                          v.getMessage().contains("obligatoire")));
    }
    
    @Test
    @DisplayName("Devrait rejeter un prix négatif")
    void testInvalidPrice_Negative() {
        // Given
        product.setPrice(new BigDecimal("-10.00"));
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("price") &&
                          v.getMessage().contains("positif")));
    }
    
    @Test
    @DisplayName("Devrait rejeter un prix à zéro")
    void testInvalidPrice_Zero() {
        // Given
        product.setPrice(BigDecimal.ZERO);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }
    
    @Test
    @DisplayName("Devrait accepter un prix positif valide")
    void testValidPrice_Positive() {
        // Given
        product.setPrice(new BigDecimal("0.01"));
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertTrue(violations.stream()
            .noneMatch(v -> v.getPropertyPath().toString().equals("price")));
    }
    
    @Test
    @DisplayName("Devrait rejeter un stock négatif")
    void testInvalidStock_Negative() {
        // Given
        product.setStock(-1);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("stock") &&
                          v.getMessage().contains("négatif")));
    }
    
    @Test
    @DisplayName("Devrait accepter un stock à zéro")
    void testValidStock_Zero() {
        // Given
        product.setStock(0);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertTrue(violations.stream()
            .noneMatch(v -> v.getPropertyPath().toString().equals("stock")));
    }
    
    @Test
    @DisplayName("Devrait rejeter une catégorie vide")
    void testInvalidCategory_Blank() {
        // Given
        product.setCategory("");
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("category") &&
                          v.getMessage().contains("obligatoire")));
    }
    
    @Test
    @DisplayName("Devrait rejeter une catégorie null")
    void testInvalidCategory_Null() {
        // Given
        product.setCategory(null);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("category")));
    }
    
    @Test
    @DisplayName("Devrait rejeter une catégorie trop longue")
    void testInvalidCategory_TooLong() {
        // Given
        String longCategory = "a".repeat(51); // Plus de 50 caractères
        product.setCategory(longCategory);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("category") &&
                          v.getMessage().contains("50 caractères")));
    }
    
    @Test
    @DisplayName("Devrait mettre à jour updatedAt lors de preUpdate")
    void testPreUpdate() throws InterruptedException {
        // Given
        LocalDateTime initialUpdatedAt = product.getUpdatedAt();
        
        // When - Simuler une mise à jour
        Thread.sleep(10); // Attendre quelques millisecondes
        product.preUpdate();
        
        // Then
        assertNotNull(product.getUpdatedAt());
        assertTrue(product.getUpdatedAt().isAfter(initialUpdatedAt));
        assertTrue(product.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
    
    @Test
    @DisplayName("Devrait générer un toString correct")
    void testToString() {
        // Given
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Description");
        product.setPrice(new BigDecimal("10.00"));
        product.setStock(50);
        product.setCategory("Test");
        
        // When
        String toString = product.toString();
        
        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Product{"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name='Test Product'"));
        assertTrue(toString.contains("description='Description'"));
        assertTrue(toString.contains("price=10.00"));
        assertTrue(toString.contains("stock=50"));
        assertTrue(toString.contains("category='Test'"));
    }
    
    @Test
    @DisplayName("Devrait gérer correctement les valeurs null dans toString")
    void testToString_WithNullValues() {
        // Given
        Product productWithNulls = new Product();
        productWithNulls.setId(1L);
        productWithNulls.setName(null);
        productWithNulls.setDescription(null);
        productWithNulls.setPrice(null);
        productWithNulls.setStock(null);
        productWithNulls.setCategory(null);
        
        // When
        String toString = productWithNulls.toString();
        
        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Product{"));
    }
}

