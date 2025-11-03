package com.qualimark.ecommerce.productService.repository;

import com.qualimark.ecommerce.productService.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Recherche des produits par catégorie
     *
     * @param category La catégorie à rechercher
     * @return Liste des produits de la catégorie
     */
    List<Product> findByCategory(String category);

    /**
     * Recherche des produits par nom (recherche partielle)
     *
     * @param name Le nom ou partie du nom à rechercher
     * @return Liste des produits correspondants
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Recherche des produits en stock
     *
     * @return Liste des produits avec stock > 0
     */
    @Query("SELECT p FROM Product p WHERE p.stock > 0")
    List<Product> findAvailableProducts();

    /**
     * Recherche des produits par plage de prix
     *
     * @param minPrice Prix minimum
     * @param maxPrice Prix maximum
     * @return Liste des produits dans la plage de prix
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    /**
     * Recherche d'un produit par nom exact
     *
     * @param name Le nom exact du produit
     * @return Le produit s'il existe
     */
    Optional<Product> findByName(String name);

    /**
     * Compte le nombre de produits par catégorie
     *
     * @param category La catégorie
     * @return Le nombre de produits
     */
    long countByCategory(String category);
}
