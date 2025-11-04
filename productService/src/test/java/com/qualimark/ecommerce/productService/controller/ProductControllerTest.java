package com.qualimark.ecommerce.productService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qualimark.ecommerce.productService.dto.ProductRequest;
import com.qualimark.ecommerce.productService.model.Product;
import com.qualimark.ecommerce.productService.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour le ProductController
 * 
 * Ces tests utilisent @WebMvcTest pour tester uniquement la couche web
 * sans charger le contexte Spring complet.
 */
@WebMvcTest(ProductController.class)
@DisplayName("Tests unitaires ProductController")
public class ProductControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductService productService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Product testProduct;
    private ProductRequest productRequest;
    
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
        
        productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("Description");
        productRequest.setPrice(new BigDecimal("10.00"));
        productRequest.setStock(50);
        productRequest.setCategory("Test");
        productRequest.setSku("TEST001");
    }
    
    @Test
    @DisplayName("Devrait récupérer tous les produits")
    void testGetAllProducts() throws Exception {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
        
        when(productService.getProductsSorted(anyString(), any(Pageable.class))).thenReturn(productPage);
        
        // When & Then
        mockMvc.perform(get("/api/v1/products")
                .param("page", "0")
                .param("size", "20")
                .param("sortBy", "name")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));
        
        verify(productService, times(1)).getProductsSorted(anyString(), any(Pageable.class));
    }
    
    @Test
    @DisplayName("Devrait récupérer un produit par ID")
    void testGetProductById() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(Optional.of(testProduct));
        
        // When & Then
        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.sku").value("TEST001"));
        
        verify(productService, times(1)).getProductById(1L);
    }
    
    @Test
    @DisplayName("Devrait retourner 404 si le produit n'existe pas")
    void testGetProductByIdNotFound() throws Exception {
        // Given
        when(productService.getProductById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/api/v1/products/999"))
                .andExpect(status().isNotFound());
        
        verify(productService, times(1)).getProductById(999L);
    }
    
    @Test
    @DisplayName("Devrait récupérer un produit par SKU")
    void testGetProductBySku() throws Exception {
        // Given
        when(productService.getProductBySku("TEST001")).thenReturn(Optional.of(testProduct));
        
        // When & Then
        mockMvc.perform(get("/api/v1/products/sku/TEST001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("TEST001"));
        
        verify(productService, times(1)).getProductBySku("TEST001");
    }
    
    @Test
    @DisplayName("Devrait créer un nouveau produit")
    void testCreateProduct() throws Exception {
        // Given
        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);
        
        // When & Then
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));
        
        verify(productService, times(1)).createProduct(any(Product.class));
    }
    
    @Test
    @DisplayName("Devrait retourner 409 si le produit existe déjà")
    void testCreateProductConflict() throws Exception {
        // Given
        when(productService.createProduct(any(Product.class)))
            .thenThrow(new IllegalArgumentException("Un produit avec ce nom existe déjà"));
        
        // When & Then
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isConflict());
        
        verify(productService, times(1)).createProduct(any(Product.class));
    }
    
    @Test
    @DisplayName("Devrait mettre à jour un produit")
    void testUpdateProduct() throws Exception {
        // Given
        Product updatedProduct = new Product(
            "Updated Product",
            "Updated Description",
            new BigDecimal("20.00"),
            100,
            "Updated Category"
        );
        updatedProduct.setId(1L);
        
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);
        
        // When & Then
        mockMvc.perform(put("/api/v1/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"));
        
        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }
    
    @Test
    @DisplayName("Devrait retourner 404 si le produit à mettre à jour n'existe pas")
    void testUpdateProductNotFound() throws Exception {
        // Given
        when(productService.updateProduct(eq(999L), any(Product.class)))
            .thenThrow(new IllegalArgumentException("Produit non trouvé"));
        
        // When & Then
        mockMvc.perform(put("/api/v1/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isNotFound());
        
        verify(productService, times(1)).updateProduct(eq(999L), any(Product.class));
    }
    
    @Test
    @DisplayName("Devrait supprimer un produit")
    void testDeleteProduct() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);
        
        // When & Then
        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());
        
        verify(productService, times(1)).deleteProduct(1L);
    }
    
    @Test
    @DisplayName("Devrait retourner 404 si le produit à supprimer n'existe pas")
    void testDeleteProductNotFound() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("Produit non trouvé"))
            .when(productService).deleteProduct(999L);
        
        // When & Then
        mockMvc.perform(delete("/api/v1/products/999"))
                .andExpect(status().isNotFound());
        
        verify(productService, times(1)).deleteProduct(999L);
    }
    
    @Test
    @DisplayName("Devrait rechercher des produits par catégorie")
    void testGetProductsByCategory() throws Exception {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
        
        when(productService.getProductsByCategory(eq("Test"), any(Pageable.class))).thenReturn(productPage);
        
        // When & Then
        mockMvc.perform(get("/api/v1/products/category/Test")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
        
        verify(productService, times(1)).getProductsByCategory(eq("Test"), any(Pageable.class));
    }
    
    @Test
    @DisplayName("Devrait rechercher des produits par nom")
    void testSearchProductsByName() throws Exception {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
        
        when(productService.searchProductsByName(eq("Test"), any(Pageable.class))).thenReturn(productPage);
        
        // When & Then
        mockMvc.perform(get("/api/v1/products/search")
                .param("name", "Test")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
        
        verify(productService, times(1)).searchProductsByName(eq("Test"), any(Pageable.class));
    }
    
    @Test
    @DisplayName("Devrait récupérer les produits disponibles")
    void testGetAvailableProducts() throws Exception {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
        
        when(productService.getAvailableProducts(any(Pageable.class))).thenReturn(productPage);
        
        // When & Then
        mockMvc.perform(get("/api/v1/products/available")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
        
        verify(productService, times(1)).getAvailableProducts(any(Pageable.class));
    }
    
    @Test
    @DisplayName("Devrait rechercher des produits par plage de prix")
    void testGetProductsByPriceRange() throws Exception {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
        
        when(productService.getProductsByPriceRange(any(BigDecimal.class), any(BigDecimal.class), any(Pageable.class))).thenReturn(productPage);
        
        // When & Then
        mockMvc.perform(get("/api/v1/products/price-range")
                .param("minPrice", "5.00")
                .param("maxPrice", "15.00")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
        
        verify(productService, times(1)).getProductsByPriceRange(any(BigDecimal.class), any(BigDecimal.class), any(Pageable.class));
    }
    
    @Test
    @DisplayName("Devrait mettre à jour le stock d'un produit")
    void testUpdateStock() throws Exception {
        // Given
        testProduct.setStock(100);
        when(productService.updateStock(1L, 100)).thenReturn(testProduct);
        
        // When & Then
        mockMvc.perform(patch("/api/v1/products/1/stock")
                .param("stock", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(100));
        
        verify(productService, times(1)).updateStock(1L, 100);
    }
    
    @Test
    @DisplayName("Devrait retourner 400 si le stock est invalide")
    void testUpdateStockInvalid() throws Exception {
        // Given
        when(productService.updateStock(1L, -10))
            .thenThrow(new IllegalArgumentException("Le stock ne peut pas être négatif"));
        
        // When & Then
        mockMvc.perform(patch("/api/v1/products/1/stock")
                .param("stock", "-10"))
                .andExpect(status().isBadRequest());
        
        verify(productService, times(1)).updateStock(1L, -10);
    }
    
    @Test
    @DisplayName("Devrait réserver du stock pour un produit")
    void testReserveStock() throws Exception {
        // Given
        testProduct.setStock(40);
        when(productService.reserveStock(1L, 10)).thenReturn(testProduct);
        
        // When & Then
        mockMvc.perform(post("/api/v1/products/1/reserve")
                .param("quantity", "10"))
                .andExpect(status().isOk());
        
        verify(productService, times(1)).reserveStock(1L, 10);
    }
    
    @Test
    @DisplayName("Devrait retourner 400 si la réservation échoue")
    void testReserveStockFailure() throws Exception {
        // Given
        when(productService.reserveStock(1L, 1000))
            .thenThrow(new IllegalStateException("Stock insuffisant"));
        
        // When & Then
        mockMvc.perform(post("/api/v1/products/1/reserve")
                .param("quantity", "1000"))
                .andExpect(status().isBadRequest());
        
        verify(productService, times(1)).reserveStock(1L, 1000);
    }
    
    @Test
    @DisplayName("Devrait libérer du stock réservé")
    void testReleaseStock() throws Exception {
        // Given
        testProduct.setStock(60);
        when(productService.releaseStock(1L, 10)).thenReturn(testProduct);
        
        // When & Then
        mockMvc.perform(post("/api/v1/products/1/release")
                .param("quantity", "10"))
                .andExpect(status().isOk());
        
        verify(productService, times(1)).releaseStock(1L, 10);
    }
    
    @Test
    @DisplayName("Devrait vérifier la disponibilité d'un produit")
    void testCheckAvailability() throws Exception {
        // Given
        when(productService.isProductAvailable(1L)).thenReturn(true);
        
        // When & Then
        mockMvc.perform(get("/api/v1/products/1/availability"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        
        verify(productService, times(1)).isProductAvailable(1L);
    }
    
    @Test
    @DisplayName("Devrait vérifier si un produit peut être réservé")
    void testCanReserve() throws Exception {
        // Given
        when(productService.canReserveProduct(1L, 10)).thenReturn(true);
        
        // When & Then
        mockMvc.perform(get("/api/v1/products/1/can-reserve")
                .param("quantity", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        
        verify(productService, times(1)).canReserveProduct(1L, 10);
    }
    
    @Test
    @DisplayName("Devrait récupérer les produits avec stock faible")
    void testGetProductsWithLowStock() throws Exception {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getProductsWithLowStock(10)).thenReturn(products);
        
        // When & Then
        mockMvc.perform(get("/api/v1/products/low-stock")
                .param("threshold", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        
        verify(productService, times(1)).getProductsWithLowStock(10);
    }
}

