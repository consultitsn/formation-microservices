package com.qualimark.ecommerce.productService.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour l'entité Product
 * 
 * Ces tests vérifient les méthodes métier de l'entité Product,
 * notamment la gestion du stock et de la disponibilité.
 */
@DisplayName("Tests unitaires Product")
public class ProductTest {
    
    private Product product;
    
    @BeforeEach
    void setUp() {
        product = new Product(
            "Test Product",
            "Description du produit",
            new BigDecimal("10.00"),
            50,
            "Test"
        );
        product.setId(1L);
        product.setIsActive(true);
    }
    
    @Test
    @DisplayName("Devrait créer un produit avec les valeurs par défaut")
    void testProductCreation() {
        // Given & When
        Product newProduct = new Product();
        
        // Then
        assertNotNull(newProduct);
        assertTrue(newProduct.getIsActive());
        assertNotNull(newProduct.getCreatedAt());
        assertNotNull(newProduct.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Devrait retourner true si le produit est disponible")
    void testIsAvailable() {
        // Given
        product.setStock(10);
        product.setIsActive(true);
        
        // When
        boolean available = product.isAvailable();
        
        // Then
        assertTrue(available);
    }
    
    @Test
    @DisplayName("Devrait retourner false si le produit n'est pas disponible (stock = 0)")
    void testIsNotAvailableZeroStock() {
        // Given
        product.setStock(0);
        product.setIsActive(true);
        
        // When
        boolean available = product.isAvailable();
        
        // Then
        assertFalse(available);
    }
    
    @Test
    @DisplayName("Devrait retourner false si le produit est inactif")
    void testIsNotAvailableInactive() {
        // Given
        product.setStock(10);
        product.setIsActive(false);
        
        // When
        boolean available = product.isAvailable();
        
        // Then
        assertFalse(available);
    }
    
    @Test
    @DisplayName("Devrait retourner true si le produit peut être réservé")
    void testCanReserve() {
        // Given
        product.setStock(50);
        product.setIsActive(true);
        
        // When
        boolean canReserve = product.canReserve(30);
        
        // Then
        assertTrue(canReserve);
    }
    
    @Test
    @DisplayName("Devrait retourner false si la quantité dépasse le stock")
    void testCanReserveInsufficientStock() {
        // Given
        product.setStock(50);
        product.setIsActive(true);
        
        // When
        boolean canReserve = product.canReserve(100);
        
        // Then
        assertFalse(canReserve);
    }
    
    @Test
    @DisplayName("Devrait retourner false si le produit est inactif")
    void testCanReserveInactive() {
        // Given
        product.setStock(50);
        product.setIsActive(false);
        
        // When
        boolean canReserve = product.canReserve(30);
        
        // Then
        assertFalse(canReserve);
    }
    
    @Test
    @DisplayName("Devrait réserver du stock avec succès")
    void testReserve() {
        // Given
        product.setStock(50);
        product.setIsActive(true);
        
        // When
        product.reserve(20);
        
        // Then
        assertEquals(30, product.getStock());
    }
    
    @Test
    @DisplayName("Devrait lancer une exception si la réservation dépasse le stock")
    void testReserveExceedsStock() {
        // Given
        product.setStock(50);
        product.setIsActive(true);
        
        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            product.reserve(100);
        });
        
        assertTrue(exception.getMessage().contains("Impossible de réserver"));
        assertEquals(50, product.getStock()); // Le stock ne doit pas avoir changé
    }
    
    @Test
    @DisplayName("Devrait lancer une exception si le produit est inactif lors de la réservation")
    void testReserveInactive() {
        // Given
        product.setStock(50);
        product.setIsActive(false);
        
        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            product.reserve(20);
        });
        
        assertTrue(exception.getMessage().contains("Impossible de réserver"));
    }
    
    @Test
    @DisplayName("Devrait libérer du stock réservé")
    void testRelease() {
        // Given
        product.setStock(30);
        
        // When
        product.release(20);
        
        // Then
        assertEquals(50, product.getStock());
    }
    
    @Test
    @DisplayName("Devrait libérer du stock même si le produit est inactif")
    void testReleaseInactive() {
        // Given
        product.setStock(30);
        product.setIsActive(false);
        
        // When
        product.release(20);
        
        // Then
        assertEquals(50, product.getStock());
    }
    
    @Test
    @DisplayName("Devrait mettre à jour correctement les champs")
    void testSettersAndGetters() {
        // Given
        Product product = new Product();
        
        // When
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Description");
        product.setPrice(new BigDecimal("15.50"));
        product.setStock(100);
        product.setCategory("Category");
        product.setSku("SKU001");
        product.setWeight(new BigDecimal("1.5"));
        product.setDimensions("10x10x10");
        product.setIsActive(true);
        
        // Then
        assertEquals(1L, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("Description", product.getDescription());
        assertEquals(new BigDecimal("15.50"), product.getPrice());
        assertEquals(100, product.getStock());
        assertEquals("Category", product.getCategory());
        assertEquals("SKU001", product.getSku());
        assertEquals(new BigDecimal("1.5"), product.getWeight());
        assertEquals("10x10x10", product.getDimensions());
        assertTrue(product.getIsActive());
    }
    
    @Test
    @DisplayName("Devrait avoir un toString correct")
    void testToString() {
        // When
        String toString = product.toString();
        
        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Test Product"));
        assertTrue(toString.contains("1"));
    }
}

