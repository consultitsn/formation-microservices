package com.qualimark.ecommerce.productService.service;

import com.qualimark.ecommerce.productService.model.Product;
import com.qualimark.ecommerce.productService.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour le ProductService
 * 
 * Ces tests démontrent les tests unitaires purs avec isolation complète
 * en utilisant des mocks pour le repository. Aucun contexte Spring n'est chargé.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires ProductService")
public class ProductServiceUnitTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductService productService;
    
    private Product testProduct;
    
    @BeforeEach
    void setUp() {
        testProduct = new Product(
            "Test Product",
            "Description du produit de test",
            new BigDecimal("10.00"),
            50,
            "Test"
        );
        testProduct.setId(1L);
    }
    
    @Test
    @DisplayName("Devrait récupérer tous les produits")
    void testGetAllProducts() {
        // Given
        Product product1 = new Product("Product 1", "Desc 1", new BigDecimal("10.00"), 10, "Cat1");
        Product product2 = new Product("Product 2", "Desc 2", new BigDecimal("20.00"), 20, "Cat2");
        List<Product> expectedProducts = Arrays.asList(product1, product2);
        
        when(productRepository.findAll()).thenReturn(expectedProducts);
        
        // When
        List<Product> products = productService.getAllProducts();
        
        // Then
        assertNotNull(products);
        assertEquals(2, products.size());
        verify(productRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Devrait récupérer un produit par ID")
    void testGetProductById() {
        // Given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        
        // When
        Optional<Product> result = productService.getProductById(productId);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Product", result.get().getName());
        verify(productRepository, times(1)).findById(productId);
    }
    
    @Test
    @DisplayName("Devrait retourner empty quand le produit n'existe pas")
    void testGetProductByIdNotFound() {
        // Given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        
        // When
        Optional<Product> result = productService.getProductById(productId);
        
        // Then
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(productId);
    }
    
    @Test
    @DisplayName("Devrait créer un nouveau produit avec succès")
    void testCreateProduct() {
        // Given
        Product newProduct = new Product(
            "Nouveau Produit",
            "Description",
            new BigDecimal("15.50"),
            25,
            "Test"
        );
        
        when(productRepository.findByName(newProduct.getName())).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // When
        Product createdProduct = productService.createProduct(newProduct);
        
        // Then
        assertNotNull(createdProduct);
        verify(productRepository, times(1)).findByName(newProduct.getName());
        verify(productRepository, times(1)).save(newProduct);
    }
    
    @Test
    @DisplayName("Devrait lancer une exception si le produit existe déjà")
    void testCreateProductWithDuplicateName() {
        // Given
        when(productRepository.findByName(testProduct.getName())).thenReturn(Optional.of(testProduct));
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(testProduct);
        });
        
        assertEquals("Un produit avec ce nom existe déjà", exception.getMessage());
        verify(productRepository, times(1)).findByName(testProduct.getName());
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Devrait mettre à jour un produit existant")
    void testUpdateProduct() {
        // Given
        Long productId = 1L;
        Product updateData = new Product(
            "Produit Modifié",
            "Nouvelle description",
            new BigDecimal("25.00"),
            75,
            "Test Modifié"
        );
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Product updatedProduct = productService.updateProduct(productId, updateData);
        
        // Then
        assertNotNull(updatedProduct);
        assertEquals("Produit Modifié", updatedProduct.getName());
        assertEquals("Nouvelle description", updatedProduct.getDescription());
        assertEquals(new BigDecimal("25.00"), updatedProduct.getPrice());
        assertEquals(75, updatedProduct.getStock());
        assertEquals("Test Modifié", updatedProduct.getCategory());
        
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(testProduct);
    }
    
    @Test
    @DisplayName("Devrait lancer une exception lors de la mise à jour d'un produit inexistant")
    void testUpdateProductNotFound() {
        // Given
        Long productId = 999L;
        Product updateData = new Product("Produit", "Desc", new BigDecimal("25.00"), 75, "Test");
        
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProduct(productId, updateData);
        });
        
        assertTrue(exception.getMessage().contains("Produit non trouvé"));
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Devrait supprimer un produit existant")
    void testDeleteProduct() {
        // Given
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(productId);
        
        // When
        productService.deleteProduct(productId);
        
        // Then
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, times(1)).deleteById(productId);
    }
    
    @Test
    @DisplayName("Devrait lancer une exception lors de la suppression d'un produit inexistant")
    void testDeleteProductNotFound() {
        // Given
        Long productId = 999L;
        when(productRepository.existsById(productId)).thenReturn(false);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.deleteProduct(productId);
        });
        
        assertTrue(exception.getMessage().contains("Produit non trouvé"));
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, never()).deleteById(anyLong());
    }
    
    @Test
    @DisplayName("Devrait récupérer les produits par catégorie")
    void testGetProductsByCategory() {
        // Given
        String category = "Test";
        List<Product> expectedProducts = Arrays.asList(testProduct);
        
        when(productRepository.findByCategory(category)).thenReturn(expectedProducts);
        
        // When
        List<Product> products = productService.getProductsByCategory(category);
        
        // Then
        assertNotNull(products);
        assertEquals(1, products.size());
        verify(productRepository, times(1)).findByCategory(category);
    }
    
    @Test
    @DisplayName("Devrait rechercher des produits par nom")
    void testSearchProductsByName() {
        // Given
        String searchTerm = "Pomme";
        Product appleProduct = new Product("Pomme Rouge", "Desc", new BigDecimal("2.50"), 20, "Fruits");
        List<Product> expectedProducts = Arrays.asList(appleProduct);
        
        when(productRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(expectedProducts);
        
        // When
        List<Product> products = productService.searchProductsByName(searchTerm);
        
        // Then
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("Pomme Rouge", products.get(0).getName());
        verify(productRepository, times(1)).findByNameContainingIgnoreCase(searchTerm);
    }
    
    @Test
    @DisplayName("Devrait récupérer les produits disponibles")
    void testGetAvailableProducts() {
        // Given
        List<Product> availableProducts = Arrays.asList(testProduct);
        
        when(productRepository.findAvailableProducts()).thenReturn(availableProducts);
        
        // When
        List<Product> products = productService.getAvailableProducts();
        
        // Then
        assertNotNull(products);
        assertEquals(1, products.size());
        verify(productRepository, times(1)).findAvailableProducts();
    }
    
    @Test
    @DisplayName("Devrait mettre à jour le stock d'un produit")
    void testUpdateStock() {
        // Given
        Long productId = 1L;
        Integer newStock = 100;
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Product updatedProduct = productService.updateStock(productId, newStock);
        
        // Then
        assertNotNull(updatedProduct);
        assertEquals(newStock, updatedProduct.getStock());
        
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(productCaptor.capture());
        assertEquals(newStock, productCaptor.getValue().getStock());
    }
    
    @Test
    @DisplayName("Devrait lancer une exception pour un stock négatif")
    void testUpdateStockWithNegativeValue() {
        // Given
        Long productId = 1L;
        Integer negativeStock = -10;
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.updateStock(productId, negativeStock);
        });
        
        assertEquals("Le stock ne peut pas être négatif", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Devrait retourner true si le produit est disponible")
    void testIsProductAvailable() {
        // Given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        
        // When
        boolean isAvailable = productService.isProductAvailable(productId);
        
        // Then
        assertTrue(isAvailable);
        verify(productRepository, times(1)).findById(productId);
    }
    
    @Test
    @DisplayName("Devrait retourner false si le produit n'est pas disponible")
    void testIsProductNotAvailable() {
        // Given
        Long productId = 1L;
        Product outOfStockProduct = new Product("Produit", "Desc", new BigDecimal("5.00"), 0, "Test");
        outOfStockProduct.setId(productId);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(outOfStockProduct));
        
        // When
        boolean isAvailable = productService.isProductAvailable(productId);
        
        // Then
        assertFalse(isAvailable);
        verify(productRepository, times(1)).findById(productId);
    }
    
    @Test
    @DisplayName("Devrait retourner false si le produit n'existe pas")
    void testIsProductAvailableNotFound() {
        // Given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        
        // When
        boolean isAvailable = productService.isProductAvailable(productId);
        
        // Then
        assertFalse(isAvailable);
        verify(productRepository, times(1)).findById(productId);
    }
}

