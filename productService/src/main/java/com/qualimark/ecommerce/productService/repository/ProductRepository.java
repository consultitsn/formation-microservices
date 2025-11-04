package com.qualimark.ecommerce.productService.repository;

import com.qualimark.ecommerce.productService.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des produits
 *
 * Cette interface illustre le pattern Repository et sera utilisée
 * par le ProductService dans l'architecture microservices.
 * Elle utilise Spring Data JPA pour simplifier l'accès aux données.
 *
 * @author Formation Microservices
 * @version 1.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Recherche des produits par catégorie avec pagination
     *
     * @param category La catégorie à rechercher
     * @param pageable Paramètres de pagination
     * @return Page des produits de la catégorie
     */
    Page<Product> findByCategoryAndIsActiveTrue(String category, Pageable pageable);

    /**
     * Recherche des produits par nom (recherche partielle) avec pagination
     *
     * @param name Le nom ou partie du nom à rechercher
     * @param pageable Paramètres de pagination
     * @return Page des produits correspondants
     */
    Page<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);

    /**
     * Recherche des produits en stock avec pagination
     *
     * @param pageable Paramètres de pagination
     * @return Page des produits avec stock > 0
     */
    @Query("SELECT p FROM Product p WHERE p.stock > 0 AND p.isActive = true")
    Page<Product> findAvailableProducts(Pageable pageable);

    /**
     * Recherche des produits par plage de prix avec pagination
     *
     * @param minPrice Prix minimum
     * @param maxPrice Prix maximum
     * @param pageable Paramètres de pagination
     * @return Page des produits dans la plage de prix
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.isActive = true")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   Pageable pageable);

    /**
     * Recherche d'un produit par nom exact
     *
     * @param name Le nom exact du produit
     * @return Le produit s'il existe
     */
    Optional<Product> findByNameAndIsActiveTrue(String name);

    /**
     * Recherche d'un produit par SKU
     *
     * @param sku Le SKU du produit
     * @return Le produit s'il existe
     */
    Optional<Product> findBySkuAndIsActiveTrue(String sku);

    /**
     * Compte le nombre de produits par catégorie
     *
     * @param category La catégorie
     * @return Le nombre de produits
     */
    long countByCategoryAndIsActiveTrue(String category);

    /**
     * Recherche des produits avec stock faible
     *
     * @param threshold Seuil de stock faible
     * @return Liste des produits avec stock <= threshold
     */
    @Query("SELECT p FROM Product p WHERE p.stock <= :threshold AND p.isActive = true")
    List<Product> findProductsWithLowStock(@Param("threshold") Integer threshold);

    /**
     * Recherche des produits par catégories multiples
     *
     * @param categories Liste des catégories
     * @param pageable Paramètres de pagination
     * @return Page des produits des catégories spécifiées
     */
    @Query("SELECT p FROM Product p WHERE p.category IN :categories AND p.isActive = true")
    Page<Product> findByCategories(@Param("categories") List<String> categories, Pageable pageable);

    /**
     * Recherche des produits actifs avec pagination
     *
     * @param pageable Paramètres de pagination
     * @return Page des produits actifs
     */
    Page<Product> findByIsActiveTrue(Pageable pageable);

    /**
     * Recherche des produits par nom et catégorie
     *
     * @param name Le nom à rechercher
     * @param category La catégorie
     * @param pageable Paramètres de pagination
     * @return Page des produits correspondants
     */
    Page<Product> findByNameContainingIgnoreCaseAndCategoryAndIsActiveTrue(
            String name, String category, Pageable pageable);

    /**
     * Recherche des produits avec tri par prix
     *
     * @param pageable Paramètres de pagination avec tri
     * @return Page des produits triés
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.price ASC")
    Page<Product> findAllActiveProductsOrderByPrice(Pageable pageable);

    /**
     * Recherche des produits avec tri par nom
     *
     * @param pageable Paramètres de pagination avec tri
     * @return Page des produits triés
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.name ASC")
    Page<Product> findAllActiveProductsOrderByName(Pageable pageable);

    /**
     * Recherche des produits avec tri par date de création
     *
     * @param pageable Paramètres de pagination avec tri
     * @return Page des produits triés
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.createdAt DESC")
    Page<Product> findAllActiveProductsOrderByCreatedAt(Pageable pageable);
}
