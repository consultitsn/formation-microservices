package com.qualimark.ecommerce.productService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qualimark.ecommerce.productService.model.Product;
import com.qualimark.ecommerce.productService.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour ProductController avec @WebMvcTest
 * 
 * Ces tests utilisent @WebMvcTest pour tester uniquement la couche web MVC
 * sans démarrer le contexte Spring complet. Le ProductService est mocké.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        testProduct = new Product(
            "Test Product",
            "Description du produit de test",
            new BigDecimal("10.00"),
            50,
            "Electronics"
        );
        testProduct.setId(1L);

        testProduct2 = new Product(
            "Test Product 2",
            "Description du produit 2",
            new BigDecimal("20.00"),
            30,
            "Books"
        );
        testProduct2.setId(2L);
    }

    @Test
    void testGetAllProducts() throws Exception {
        // Given
        List<Product> products = Arrays.asList(testProduct, testProduct2);
        when(productService.getAllProducts()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$[0].price").value(10.00))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Test Product 2"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetProductById_Success() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(Optional.of(testProduct));

        // When & Then
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(10.00))
                .andExpect(jsonPath("$.stock").value(50))
                .andExpect(jsonPath("$.category").value("Electronics"));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
        // Given
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById(999L);
    }

    @Test
    void testCreateProduct_Success() throws Exception {
        // Given
        Product newProduct = new Product(
            "New Product",
            "New Description",
            new BigDecimal("15.50"),
            25,
            "Food"
        );
        Product savedProduct = new Product(
            "New Product",
            "New Description",
            new BigDecimal("15.50"),
            25,
            "Food"
        );
        savedProduct.setId(3L);

        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.price").value(15.50))
                .andExpect(jsonPath("$.stock").value(25));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void testCreateProduct_Conflict() throws Exception {
        // Given
        Product duplicateProduct = new Product(
            "Duplicate Product",
            "Description",
            new BigDecimal("10.00"),
            5,
            "Test"
        );

        when(productService.createProduct(any(Product.class)))
                .thenThrow(new IllegalArgumentException("Un produit avec ce nom existe déjà"));

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateProduct)))
                .andExpect(status().isConflict());

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void testCreateProduct_ValidationError() throws Exception {
        // Given - Product avec des champs invalides (nom vide)
        Product invalidProduct = new Product(
            "", // Nom vide - devrait échouer la validation
            "Description",
            new BigDecimal("10.00"),
            5,
            "Test"
        );

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    void testUpdateProduct_Success() throws Exception {
        // Given
        Product updateData = new Product(
            "Updated Product",
            "Updated Description",
            new BigDecimal("25.00"),
            75,
            "Updated Category"
        );
        Product updatedProduct = new Product(
            "Updated Product",
            "Updated Description",
            new BigDecimal("25.00"),
            75,
            "Updated Category"
        );
        updatedProduct.setId(1L);

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        // When & Then
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(25.00))
                .andExpect(jsonPath("$.stock").value(75));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() throws Exception {
        // Given
        Product updateData = new Product(
            "Updated Product",
            "Description",
            new BigDecimal("25.00"),
            75,
            "Test"
        );

        when(productService.updateProduct(eq(999L), any(Product.class)))
                .thenThrow(new IllegalArgumentException("Produit non trouvé avec l'ID : 999"));

        // When & Then
        mockMvc.perform(put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).updateProduct(eq(999L), any(Product.class));
    }

    @Test
    void testDeleteProduct_Success() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void testDeleteProduct_NotFound() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("Produit non trouvé avec l'ID : 999"))
                .when(productService).deleteProduct(999L);

        // When & Then
        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).deleteProduct(999L);
    }

    @Test
    void testGetProductsByCategory() throws Exception {
        // Given
        List<Product> electronicsProducts = Arrays.asList(testProduct);
        when(productService.getProductsByCategory("Electronics")).thenReturn(electronicsProducts);

        // When & Then
        mockMvc.perform(get("/api/products/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value("Electronics"));

        verify(productService, times(1)).getProductsByCategory("Electronics");
    }

    @Test
    void testSearchProductsByName() throws Exception {
        // Given
        List<Product> searchResults = Arrays.asList(testProduct);
        when(productService.searchProductsByName("Test")).thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/api/products/search")
                .param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Product"));

        verify(productService, times(1)).searchProductsByName("Test");
    }

    @Test
    void testGetAvailableProducts() throws Exception {
        // Given
        List<Product> availableProducts = Arrays.asList(testProduct);
        when(productService.getAvailableProducts()).thenReturn(availableProducts);

        // When & Then
        mockMvc.perform(get("/api/products/available"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].stock").value(50));

        verify(productService, times(1)).getAvailableProducts();
    }

    @Test
    void testUpdateStock_Success() throws Exception {
        // Given
        Product updatedProduct = new Product(
            "Test Product",
            "Description du produit de test",
            new BigDecimal("10.00"),
            100, // Nouveau stock
            "Electronics"
        );
        updatedProduct.setId(1L);

        when(productService.updateStock(1L, 100)).thenReturn(updatedProduct);

        // When & Then
        mockMvc.perform(patch("/api/products/1/stock")
                .param("stock", "100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.stock").value(100));

        verify(productService, times(1)).updateStock(1L, 100);
    }

    @Test
    void testUpdateStock_BadRequest() throws Exception {
        // Given
        when(productService.updateStock(1L, -10))
                .thenThrow(new IllegalArgumentException("Le stock ne peut pas être négatif"));

        // When & Then
        mockMvc.perform(patch("/api/products/1/stock")
                .param("stock", "-10"))
                .andExpect(status().isBadRequest());

        verify(productService, times(1)).updateStock(1L, -10);
    }

    @Test
    void testCheckAvailability_Available() throws Exception {
        // Given
        when(productService.isProductAvailable(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/products/1/availability"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));

        verify(productService, times(1)).isProductAvailable(1L);
    }

    @Test
    void testCheckAvailability_NotAvailable() throws Exception {
        // Given
        when(productService.isProductAvailable(2L)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/products/2/availability"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(false));

        verify(productService, times(1)).isProductAvailable(2L);
    }
}

