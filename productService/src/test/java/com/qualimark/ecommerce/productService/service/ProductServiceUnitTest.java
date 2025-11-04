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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private Pageable pageable;
    
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
        testProduct.setSku("TEST001");
        testProduct.setIsActive(true);
        
        pageable = PageRequest.of(0, 20);
    }
    
    @Test
    @DisplayName("Devrait récupérer tous les produits avec pagination")
    void testGetAllProducts() {
        // Given
        Product product1 = new Product("Product 1", "Desc 1", new BigDecimal("10.00"), 10, "Cat1");
        Product product2 = new Product("Product 2", "Desc 2", new BigDecimal("20.00"), 20, "Cat2");
        List<Product> products = Arrays.asList(product1, product2);
        Page<Product> productPage = new PageImpl<>(products);
        
        when(productRepository.findByIsActiveTrue(pageable)).thenReturn(productPage);
        
        // When
        Page<Product> result = productService.getAllProducts(pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(productRepository, times(1)).findByIsActiveTrue(pageable);
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
    @DisplayName("Devrait retourner empty quand le produit est inactif")
    void testGetProductByIdInactive() {
        // Given
        Long productId = 1L;
        testProduct.setIsActive(false);
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        
        // When
        Optional<Product> result = productService.getProductById(productId);
        
        // Then
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(productId);
    }
    
    @Test
    @DisplayName("Devrait récupérer un produit par SKU")
    void testGetProductBySku() {
        // Given
        String sku = "TEST001";
        when(productRepository.findBySkuAndIsActiveTrue(sku)).thenReturn(Optional.of(testProduct));
        
        // When
        Optional<Product> result = productService.getProductBySku(sku);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals("TEST001", result.get().getSku());
        verify(productRepository, times(1)).findBySkuAndIsActiveTrue(sku);
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
        
        when(productRepository.findByNameAndIsActiveTrue(newProduct.getName())).thenReturn(Optional.empty());
        when(productRepository.count()).thenReturn(0L);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // When
        Product createdProduct = productService.createProduct(newProduct);
        
        // Then
        assertNotNull(createdProduct);
        verify(productRepository, times(1)).findByNameAndIsActiveTrue(newProduct.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Devrait générer un SKU automatiquement si non fourni")
    void testCreateProductWithAutoGeneratedSku() {
        // Given
        Product newProduct = new Product(
            "Nouveau Produit",
            "Description",
            new BigDecimal("15.50"),
            25,
            "Test"
        );
        newProduct.setSku(null);
        
        when(productRepository.findByNameAndIsActiveTrue(newProduct.getName())).thenReturn(Optional.empty());
        when(productRepository.count()).thenReturn(5L);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Product createdProduct = productService.createProduct(newProduct);
        
        // Then
        assertNotNull(createdProduct.getSku());
        assertTrue(createdProduct.getSku().matches("^[A-Z0-9]+\\d{3}$"));
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Devrait lancer une exception si le produit existe déjà")
    void testCreateProductWithDuplicateName() {
        // Given
        when(productRepository.findByNameAndIsActiveTrue(testProduct.getName())).thenReturn(Optional.of(testProduct));
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(testProduct);
        });
        
        assertEquals("Un produit avec ce nom existe déjà", exception.getMessage());
        verify(productRepository, times(1)).findByNameAndIsActiveTrue(testProduct.getName());
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Devrait lancer une exception si le SKU existe déjà")
    void testCreateProductWithDuplicateSku() {
        // Given
        Product newProduct = new Product("Nouveau Produit", "Desc", new BigDecimal("10.00"), 10, "Test");
        newProduct.setSku("EXISTING-SKU");
        
        when(productRepository.findByNameAndIsActiveTrue(newProduct.getName())).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsActiveTrue("EXISTING-SKU")).thenReturn(Optional.of(testProduct));
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(newProduct);
        });
        
        assertEquals("Un produit avec ce SKU existe déjà", exception.getMessage());
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
        updateData.setSku("UPDATED-SKU");
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.findByNameAndIsActiveTrue(updateData.getName())).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsActiveTrue(updateData.getSku())).thenReturn(Optional.empty());
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
        assertEquals("UPDATED-SKU", updatedProduct.getSku());
        
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
    @DisplayName("Devrait supprimer un produit (soft delete)")
    void testDeleteProduct() {
        // Given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        productService.deleteProduct(productId);
        
        // Then
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(productCaptor.capture());
        assertFalse(productCaptor.getValue().getIsActive());
        verify(productRepository, times(1)).findById(productId);
    }
    
    @Test
    @DisplayName("Devrait lancer une exception lors de la suppression d'un produit inexistant")
    void testDeleteProductNotFound() {
        // Given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.deleteProduct(productId);
        });
        
        assertTrue(exception.getMessage().contains("Produit non trouvé"));
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Devrait récupérer les produits par catégorie")
    void testGetProductsByCategory() {
        // Given
        String category = "Test";
        Product product1 = new Product("Product 1", "Desc 1", new BigDecimal("10.00"), 10, category);
        List<Product> products = Arrays.asList(product1);
        Page<Product> productPage = new PageImpl<>(products);
        
        when(productRepository.findByCategoryAndIsActiveTrue(category, pageable)).thenReturn(productPage);
        
        // When
        Page<Product> result = productService.getProductsByCategory(category, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productRepository, times(1)).findByCategoryAndIsActiveTrue(category, pageable);
    }
    
    @Test
    @DisplayName("Devrait rechercher des produits par nom")
    void testSearchProductsByName() {
        // Given
        String searchTerm = "Pomme";
        Product appleProduct = new Product("Pomme Rouge", "Desc", new BigDecimal("2.50"), 20, "Fruits");
        List<Product> products = Arrays.asList(appleProduct);
        Page<Product> productPage = new PageImpl<>(products);
        
        when(productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(searchTerm, pageable))
            .thenReturn(productPage);
        
        // When
        Page<Product> result = productService.searchProductsByName(searchTerm, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Pomme Rouge", result.getContent().get(0).getName());
        verify(productRepository, times(1)).findByNameContainingIgnoreCaseAndIsActiveTrue(searchTerm, pageable);
    }
    
    @Test
    @DisplayName("Devrait récupérer les produits disponibles")
    void testGetAvailableProducts() {
        // Given
        List<Product> availableProducts = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(availableProducts);
        
        when(productRepository.findAvailableProducts(pageable)).thenReturn(productPage);
        
        // When
        Page<Product> result = productService.getAvailableProducts(pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productRepository, times(1)).findAvailableProducts(pageable);
    }
    
    @Test
    @DisplayName("Devrait rechercher des produits par plage de prix")
    void testGetProductsByPriceRange() {
        // Given
        BigDecimal minPrice = new BigDecimal("10.00");
        BigDecimal maxPrice = new BigDecimal("50.00");
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products);
        
        when(productRepository.findByPriceRange(minPrice, maxPrice, pageable)).thenReturn(productPage);
        
        // When
        Page<Product> result = productService.getProductsByPriceRange(minPrice, maxPrice, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productRepository, times(1)).findByPriceRange(minPrice, maxPrice, pageable);
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
    @DisplayName("Devrait réserver du stock pour un produit")
    void testReserveStock() {
        // Given
        Long productId = 1L;
        Integer quantity = 10;
        testProduct.setStock(50);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Product updatedProduct = productService.reserveStock(productId, quantity);
        
        // Then
        assertNotNull(updatedProduct);
        assertEquals(40, updatedProduct.getStock());
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Devrait lancer une exception si la réservation dépasse le stock")
    void testReserveStockExceedsAvailable() {
        // Given
        Long productId = 1L;
        Integer quantity = 100;
        testProduct.setStock(50);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        
        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            productService.reserveStock(productId, quantity);
        });
        
        assertTrue(exception.getMessage().contains("Impossible de réserver"));
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Devrait libérer du stock réservé")
    void testReleaseStock() {
        // Given
        Long productId = 1L;
        Integer quantity = 10;
        testProduct.setStock(40);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Product updatedProduct = productService.releaseStock(productId, quantity);
        
        // Then
        assertNotNull(updatedProduct);
        assertEquals(50, updatedProduct.getStock());
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Devrait retourner true si le produit est disponible")
    void testIsProductAvailable() {
        // Given
        Long productId = 1L;
        testProduct.setStock(10);
        testProduct.setIsActive(true);
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
        testProduct.setStock(0);
        testProduct.setIsActive(true);
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        
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
    
    @Test
    @DisplayName("Devrait vérifier si un produit peut être réservé")
    void testCanReserveProduct() {
        // Given
        Long productId = 1L;
        Integer quantity = 10;
        testProduct.setStock(50);
        testProduct.setIsActive(true);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        
        // When
        boolean canReserve = productService.canReserveProduct(productId, quantity);
        
        // Then
        assertTrue(canReserve);
        verify(productRepository, times(1)).findById(productId);
    }
    
    @Test
    @DisplayName("Devrait retourner false si la quantité dépasse le stock")
    void testCanReserveProductInsufficientStock() {
        // Given
        Long productId = 1L;
        Integer quantity = 100;
        testProduct.setStock(50);
        testProduct.setIsActive(true);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        
        // When
        boolean canReserve = productService.canReserveProduct(productId, quantity);
        
        // Then
        assertFalse(canReserve);
        verify(productRepository, times(1)).findById(productId);
    }
    
    @Test
    @DisplayName("Devrait récupérer les produits avec stock faible")
    void testGetProductsWithLowStock() {
        // Given
        Integer threshold = 10;
        Product lowStockProduct = new Product("Low Stock", "Desc", new BigDecimal("5.00"), 5, "Test");
        List<Product> products = Arrays.asList(lowStockProduct);
        
        when(productRepository.findProductsWithLowStock(threshold)).thenReturn(products);
        
        // When
        List<Product> result = productService.getProductsWithLowStock(threshold);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findProductsWithLowStock(threshold);
    }
    
    @Test
    @DisplayName("Devrait récupérer les produits par catégories multiples")
    void testGetProductsByCategories() {
        // Given
        List<String> categories = Arrays.asList("Fruits", "Légumes");
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products);
        
        when(productRepository.findByCategories(categories, pageable)).thenReturn(productPage);
        
        // When
        Page<Product> result = productService.getProductsByCategories(categories, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productRepository, times(1)).findByCategories(categories, pageable);
    }
    
    @Test
    @DisplayName("Devrait trier les produits par prix")
    void testGetProductsSortedByPrice() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products);
        
        when(productRepository.findAllActiveProductsOrderByPrice(pageable)).thenReturn(productPage);
        
        // When
        Page<Product> result = productService.getProductsSorted("price", pageable);
        
        // Then
        assertNotNull(result);
        verify(productRepository, times(1)).findAllActiveProductsOrderByPrice(pageable);
    }
    
    @Test
    @DisplayName("Devrait trier les produits par nom")
    void testGetProductsSortedByName() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products);
        
        when(productRepository.findAllActiveProductsOrderByName(pageable)).thenReturn(productPage);
        
        // When
        Page<Product> result = productService.getProductsSorted("name", pageable);
        
        // Then
        assertNotNull(result);
        verify(productRepository, times(1)).findAllActiveProductsOrderByName(pageable);
    }
    
    @Test
    @DisplayName("Devrait trier les produits par date de création")
    void testGetProductsSortedByCreated() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products);
        
        when(productRepository.findAllActiveProductsOrderByCreatedAt(pageable)).thenReturn(productPage);
        
        // When
        Page<Product> result = productService.getProductsSorted("created", pageable);
        
        // Then
        assertNotNull(result);
        verify(productRepository, times(1)).findAllActiveProductsOrderByCreatedAt(pageable);
    }
    
    @Test
    @DisplayName("Devrait utiliser le tri par défaut pour un critère inconnu")
    void testGetProductsSortedByUnknown() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products);
        
        when(productRepository.findByIsActiveTrue(pageable)).thenReturn(productPage);
        
        // When
        Page<Product> result = productService.getProductsSorted("unknown", pageable);
        
        // Then
        assertNotNull(result);
        verify(productRepository, times(1)).findByIsActiveTrue(pageable);
    }
}

