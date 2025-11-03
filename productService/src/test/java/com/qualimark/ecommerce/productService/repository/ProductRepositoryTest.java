package com.qualimark.ecommerce.productService.repository;

import com.qualimark.ecommerce.productService.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour ProductRepository
 * 
 * Ces tests utilisent @DataJpaTest pour tester les méthodes du repository
 * avec une base de données en mémoire (H2) sans démarrer le contexte Spring complet.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests d'intégration ProductRepository")
class ProductRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private ProductRepository productRepository;
    
    private Product product1;
    private Product product2;
    private Product product3;
    private Product product4;
    
    @BeforeEach
    void setUp() {
        // Nettoyer la base de données avant chaque test
        productRepository.deleteAll();
        
        // Créer des produits de test
        product1 = new Product(
            "Laptop",
            "Ordinateur portable",
            new BigDecimal("999.99"),
            10,
            "Electronics"
        );
        
        product2 = new Product(
            "Laptop Pro",
            "Ordinateur portable professionnel",
            new BigDecimal("1499.99"),
            5,
            "Electronics"
        );
        
        product3 = new Product(
            "Book Java",
            "Livre sur Java",
            new BigDecimal("29.99"),
            0, // En rupture de stock
            "Books"
        );
        
        product4 = new Product(
            "Smartphone",
            "Téléphone intelligent",
            new BigDecimal("599.99"),
            20,
            "Electronics"
        );
        
        // Sauvegarder les produits
        product1 = entityManager.persistFlushFind(product1);
        product2 = entityManager.persistFlushFind(product2);
        product3 = entityManager.persistFlushFind(product3);
        product4 = entityManager.persistFlushFind(product4);
    }
    
    @Test
    @DisplayName("Devrait trouver tous les produits")
    void testFindAll() {
        // When
        List<Product> products = productRepository.findAll();
        
        // Then
        assertEquals(4, products.size());
    }
    
    @Test
    @DisplayName("Devrait trouver un produit par ID")
    void testFindById() {
        // When
        Optional<Product> found = productRepository.findById(product1.getId());
        
        // Then
        assertTrue(found.isPresent());
        assertEquals("Laptop", found.get().getName());
        assertEquals("Electronics", found.get().getCategory());
    }
    
    @Test
    @DisplayName("Devrait retourner empty pour un ID inexistant")
    void testFindById_NotFound() {
        // When
        Optional<Product> found = productRepository.findById(999L);
        
        // Then
        assertFalse(found.isPresent());
    }
    
    @Test
    @DisplayName("Devrait sauvegarder un nouveau produit")
    void testSave() {
        // Given
        Product newProduct = new Product(
            "Tablet",
            "Tablette",
            new BigDecimal("399.99"),
            15,
            "Electronics"
        );
        
        // When
        Product saved = productRepository.save(newProduct);
        
        // Then
        assertNotNull(saved.getId());
        assertEquals("Tablet", saved.getName());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Devrait supprimer un produit")
    void testDelete() {
        // Given
        Long productId = product1.getId();
        
        // When
        productRepository.deleteById(productId);
        
        // Then
        Optional<Product> deleted = productRepository.findById(productId);
        assertFalse(deleted.isPresent());
    }
    
    @Test
    @DisplayName("Devrait trouver des produits par catégorie")
    void testFindByCategory() {
        // When
        List<Product> electronics = productRepository.findByCategory("Electronics");
        List<Product> books = productRepository.findByCategory("Books");
        
        // Then
        assertEquals(3, electronics.size());
        assertTrue(electronics.stream().allMatch(p -> "Electronics".equals(p.getCategory())));
        
        assertEquals(1, books.size());
        assertEquals("Book Java", books.get(0).getName());
    }
    
    @Test
    @DisplayName("Devrait retourner une liste vide pour une catégorie inexistante")
    void testFindByCategory_NotFound() {
        // When
        List<Product> results = productRepository.findByCategory("NonExistent");
        
        // Then
        assertTrue(results.isEmpty());
    }
    
    @Test
    @DisplayName("Devrait rechercher des produits par nom (insensible à la casse)")
    void testFindByNameContainingIgnoreCase() {
        // When
        List<Product> laptopProducts = productRepository.findByNameContainingIgnoreCase("laptop");
        List<Product> LAPTOPProducts = productRepository.findByNameContainingIgnoreCase("LAPTOP");
        List<Product> partialMatch = productRepository.findByNameContainingIgnoreCase("lap");
        
        // Then
        assertEquals(2, laptopProducts.size());
        assertTrue(laptopProducts.stream().anyMatch(p -> "Laptop".equals(p.getName())));
        assertTrue(laptopProducts.stream().anyMatch(p -> "Laptop Pro".equals(p.getName())));
        
        assertEquals(2, LAPTOPProducts.size()); // Insensible à la casse
        
        assertEquals(2, partialMatch.size()); // Recherche partielle
    }
    
    @Test
    @DisplayName("Devrait retourner une liste vide pour un nom non trouvé")
    void testFindByNameContainingIgnoreCase_NotFound() {
        // When
        List<Product> results = productRepository.findByNameContainingIgnoreCase("NonExistent");
        
        // Then
        assertTrue(results.isEmpty());
    }
    
    @Test
    @DisplayName("Devrait trouver uniquement les produits disponibles (stock > 0)")
    void testFindAvailableProducts() {
        // When
        List<Product> available = productRepository.findAvailableProducts();
        
        // Then
        assertEquals(3, available.size()); // product3 a un stock de 0, donc exclu
        assertTrue(available.stream().allMatch(p -> p.getStock() > 0));
        assertTrue(available.stream().noneMatch(p -> "Book Java".equals(p.getName())));
    }
    
    @Test
    @DisplayName("Devrait trouver des produits par plage de prix")
    void testFindByPriceRange() {
        // When
        List<Product> productsInRange = productRepository.findByPriceRange(500.0, 1000.0);
        
        // Then
        assertEquals(2, productsInRange.size());
        assertTrue(productsInRange.stream().anyMatch(p -> "Laptop".equals(p.getName())));
        assertTrue(productsInRange.stream().anyMatch(p -> "Smartphone".equals(p.getName())));
    }
    
    @Test
    @DisplayName("Devrait retourner une liste vide pour une plage de prix sans résultats")
    void testFindByPriceRange_NoResults() {
        // When
        List<Product> productsInRange = productRepository.findByPriceRange(1.0, 10.0);
        
        // Then
        assertTrue(productsInRange.isEmpty());
    }
    
    @Test
    @DisplayName("Devrait inclure les bornes de la plage de prix")
    void testFindByPriceRange_InclusiveBounds() {
        // When - Test avec des bornes exactes
        List<Product> products = productRepository.findByPriceRange(29.99, 999.99);
        
        // Then
        assertTrue(products.size() >= 2);
        assertTrue(products.stream().anyMatch(p -> p.getPrice().equals(new BigDecimal("29.99"))));
        assertTrue(products.stream().anyMatch(p -> p.getPrice().equals(new BigDecimal("999.99"))));
    }
    
    @Test
    @DisplayName("Devrait trouver un produit par nom exact")
    void testFindByName() {
        // When
        Optional<Product> found = productRepository.findByName("Laptop");
        
        // Then
        assertTrue(found.isPresent());
        assertEquals("Laptop", found.get().getName());
        assertEquals("Electronics", found.get().getCategory());
    }
    
    @Test
    @DisplayName("Devrait retourner empty pour un nom inexistant")
    void testFindByName_NotFound() {
        // When
        Optional<Product> found = productRepository.findByName("NonExistent");
        
        // Then
        assertFalse(found.isPresent());
    }
    
    @Test
    @DisplayName("Devrait être sensible à la casse pour findByName")
    void testFindByName_CaseSensitive() {
        // When
        Optional<Product> found = productRepository.findByName("laptop"); // minuscule
        
        // Then
        assertFalse(found.isPresent()); // "Laptop" avec majuscule n'est pas trouvé
    }
    
    @Test
    @DisplayName("Devrait compter les produits par catégorie")
    void testCountByCategory() {
        // When
        long electronicsCount = productRepository.countByCategory("Electronics");
        long booksCount = productRepository.countByCategory("Books");
        long nonExistentCount = productRepository.countByCategory("NonExistent");
        
        // Then
        assertEquals(3, electronicsCount);
        assertEquals(1, booksCount);
        assertEquals(0, nonExistentCount);
    }
    
    @Test
    @DisplayName("Devrait vérifier l'existence d'un produit")
    void testExistsById() {
        // When
        boolean exists = productRepository.existsById(product1.getId());
        boolean notExists = productRepository.existsById(999L);
        
        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }
    
    @Test
    @DisplayName("Devrait mettre à jour un produit existant")
    void testUpdateProduct() {
        // Given
        product1.setName("Laptop Updated");
        product1.setPrice(new BigDecimal("1099.99"));
        product1.setStock(15);
        
        // When
        Product updated = productRepository.save(product1);
        
        // Then
        assertEquals("Laptop Updated", updated.getName());
        assertEquals(new BigDecimal("1099.99"), updated.getPrice());
        assertEquals(15, updated.getStock());
        assertEquals(product1.getId(), updated.getId());
    }
    
    @Test
    @DisplayName("Devrait maintenir l'ID lors de la mise à jour")
    void testUpdateProduct_MaintainsId() {
        // Given
        Long originalId = product1.getId();
        product1.setName("Updated Name");
        
        // When
        Product updated = productRepository.save(product1);
        
        // Then
        assertEquals(originalId, updated.getId());
    }
    
    @Test
    @DisplayName("Devrait gérer les caractères spéciaux dans les recherches")
    void testFindByNameContainingIgnoreCase_SpecialCharacters() {
        // Given
        Product specialProduct = new Product(
            "Café & Thé",
            "Description",
            new BigDecimal("10.00"),
            10,
            "Test"
        );
        entityManager.persistFlushFind(specialProduct);
        
        // When
        List<Product> results = productRepository.findByNameContainingIgnoreCase("Café");
        
        // Then
        assertEquals(1, results.size());
        assertEquals("Café & Thé", results.get(0).getName());
    }
}

