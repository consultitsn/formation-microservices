package com.qualimark.ecommerce.productService.service;

import com.qualimark.ecommerce.productService.exception.ResourceAlreadyExistException;
import com.qualimark.ecommerce.productService.exception.ResourceNotFoundException;
import com.qualimark.ecommerce.productService.model.Product;
import com.qualimark.ecommerce.productService.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Récupère tous les produits avec pagination
     *
     * @param pageable Paramètres de pagination
     * @return Page de tous les produits actifs
     */
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findByIsActiveTrue(pageable);
    }

    /**
     * Récupère un produit par son ID
     *
     * @param id L'ID du produit
     * @return Le produit s'il existe
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id)
                .filter(Product::getIsActive);
    }

    /**
     * Récupère un produit par son SKU
     *
     * @param sku Le SKU du produit
     * @return Le produit s'il existe
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySkuAndIsActiveTrue(sku);
    }

    /**
     * Crée un nouveau produit
     *
     * @param product Le produit à créer
     * @return Le produit créé
     */
    public Product createProduct(Product product) {
        // Vérification que le produit n'existe pas déjà
        if (productRepository.findByNameAndIsActiveTrue(product.getName()).isPresent()) {
            throw new ResourceAlreadyExistException("Un produit avec ce nom existe déjà");
        }

        // Génération automatique du SKU si non fourni
        if (product.getSku() == null || product.getSku().trim().isEmpty()) {
            product.setSku(generateSku(product.getName()));
        } else {
            // Vérification que le SKU n'existe pas déjà
            if (productRepository.findBySkuAndIsActiveTrue(product.getSku()).isPresent()) {
                throw new ResourceAlreadyExistException("Un produit avec ce SKU existe déjà");
            }
        }

        return productRepository.save(product);
    }

    /**
     * Met à jour un produit existant
     *
     * @param id L'ID du produit à mettre à jour
     * @param productDetails Les nouvelles informations du produit
     * @return Le produit mis à jour
     */
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .filter(Product::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID : " + id));

        // Vérification que le nouveau nom n'existe pas déjà (si changé)
        if (!product.getName().equals(productDetails.getName())) {
            if (productRepository.findByNameAndIsActiveTrue(productDetails.getName()).isPresent()) {
                throw new ResourceAlreadyExistException("Un produit avec ce nom existe déjà");
            }
        }

        // Vérification que le nouveau SKU n'existe pas déjà (si changé)
        if (productDetails.getSku() != null && !product.getSku().equals(productDetails.getSku())) {
            if (productRepository.findBySkuAndIsActiveTrue(productDetails.getSku()).isPresent()) {
                throw new ResourceAlreadyExistException("Un produit avec ce SKU existe déjà");
            }
        }

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        product.setCategory(productDetails.getCategory());
        product.setSku(productDetails.getSku());
        product.setWeight(productDetails.getWeight());
        product.setDimensions(productDetails.getDimensions());

        return productRepository.save(product);
    }

    /**
     * Supprime un produit (soft delete)
     *
     * @param id L'ID du produit à supprimer
     */
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .filter(Product::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID : " + id));

        product.setIsActive(false);
        productRepository.save(product);
    }

    /**
     * Recherche des produits par catégorie
     *
     * @param category La catégorie à rechercher
     * @param pageable Paramètres de pagination
     * @return Page des produits de la catégorie
     */
    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategoryAndIsActiveTrue(category, pageable);
    }

    /**
     * Recherche des produits par nom
     *
     * @param name Le nom à rechercher
     * @param pageable Paramètres de pagination
     * @return Page des produits correspondants
     */
    @Transactional(readOnly = true)
    public Page<Product> searchProductsByName(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name, pageable);
    }

    /**
     * Récupère les produits disponibles (en stock)
     *
     * @param pageable Paramètres de pagination
     * @return Page des produits en stock
     */
    @Transactional(readOnly = true)
    public Page<Product> getAvailableProducts(Pageable pageable) {
        return productRepository.findAvailableProducts(pageable);
    }

    /**
     * Recherche des produits par plage de prix
     *
     * @param minPrice Prix minimum
     * @param maxPrice Prix maximum
     * @param pageable Paramètres de pagination
     * @return Page des produits dans la plage de prix
     */
    @Transactional(readOnly = true)
    public Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }

    /**
     * Met à jour le stock d'un produit
     *
     * @param id L'ID du produit
     * @param newStock Le nouveau stock
     * @return Le produit mis à jour
     */
    public Product updateStock(Long id, Integer newStock) {
        Product product = productRepository.findById(id)
                .filter(Product::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID : " + id));

        if (newStock < 0) {
            throw new IllegalArgumentException("Le stock ne peut pas être négatif");
        }

        product.setStock(newStock);
        return productRepository.save(product);
    }

    /**
     * Réserve du stock pour un produit
     *
     * @param id L'ID du produit
     * @param quantity La quantité à réserver
     * @return Le produit mis à jour
     */
    public Product reserveStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .filter(Product::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID : " + id));

        product.reserve(quantity);
        return productRepository.save(product);
    }

    /**
     * Libère du stock réservé pour un produit
     *
     * @param id L'ID du produit
     * @param quantity La quantité à libérer
     * @return Le produit mis à jour
     */
    public Product releaseStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .filter(Product::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID : " + id));

        product.release(quantity);
        return productRepository.save(product);
    }

    /**
     * Vérifie si un produit est disponible
     *
     * @param id L'ID du produit
     * @return true si le produit est en stock
     */
    @Transactional(readOnly = true)
    public boolean isProductAvailable(Long id) {
        return productRepository.findById(id)
                .filter(Product::getIsActive)
                .map(Product::isAvailable)
                .orElse(false);
    }

    /**
     * Vérifie si un produit peut être réservé
     *
     * @param id L'ID du produit
     * @param quantity La quantité à réserver
     * @return true si le produit peut être réservé
     */
    @Transactional(readOnly = true)
    public boolean canReserveProduct(Long id, Integer quantity) {
        return productRepository.findById(id)
                .filter(Product::getIsActive)
                .map(product -> product.canReserve(quantity))
                .orElse(false);
    }

    /**
     * Récupère les produits avec stock faible
     *
     * @param threshold Seuil de stock faible
     * @return Liste des produits avec stock <= threshold
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsWithLowStock(Integer threshold) {
        return productRepository.findProductsWithLowStock(threshold);
    }

    /**
     * Recherche des produits par catégories multiples
     *
     * @param categories Liste des catégories
     * @param pageable Paramètres de pagination
     * @return Page des produits des catégories spécifiées
     */
    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategories(List<String> categories, Pageable pageable) {
        return productRepository.findByCategories(categories, pageable);
    }

    /**
     * Recherche des produits avec tri
     *
     * @param sortBy Critère de tri (price, name, createdAt)
     * @param pageable Paramètres de pagination
     * @return Page des produits triés
     */
    @Transactional(readOnly = true)
    public Page<Product> getProductsSorted(String sortBy, Pageable pageable) {
        return switch (sortBy.toLowerCase()) {
            case "price" -> productRepository.findAllActiveProductsOrderByPrice(pageable);
            case "name" -> productRepository.findAllActiveProductsOrderByName(pageable);
            case "created" -> productRepository.findAllActiveProductsOrderByCreatedAt(pageable);
            default -> productRepository.findByIsActiveTrue(pageable);
        };
    }

    /**
     * Génère un SKU automatique basé sur le nom du produit
     *
     * @param productName Le nom du produit
     * @return Le SKU généré
     */
    private String generateSku(String productName) {
        String baseSku = productName.toUpperCase()
                .replaceAll("[^A-Z0-9]", "")
                .substring(0, Math.min(8, productName.length()));

        // Ajouter un suffixe numérique pour éviter les doublons
        long count = productRepository.count();
        return baseSku + String.format("%03d", count + 1);
    }

}
