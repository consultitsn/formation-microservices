package com.qualimark.ecommerce.productService.controller;

import com.qualimark.ecommerce.productService.dto.ProductRequest;
import com.qualimark.ecommerce.productService.dto.ProductResponse;
import com.qualimark.ecommerce.productService.exception.ResourceNotFoundException;
import com.qualimark.ecommerce.productService.model.Product;
import com.qualimark.ecommerce.productService.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Récupère tous les produits avec pagination
     */
    @GetMapping
    @Operation(summary = "Récupère tous les produits", description = "Retourne la liste paginée de tous les produits actifs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des produits récupérée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @Parameter(description = "Numéro de page (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Critère de tri") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Direction du tri (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> products = productService.getProductsSorted(sortBy, pageable);
        Page<ProductResponse> response = products.map(ProductResponse::new);

        return ResponseEntity.ok(response);
    }

    /**
     * Récupère un produit par son ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupère un produit par ID", description = "Retourne un produit spécifique par son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit trouvé"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "ID du produit à récupérer") @PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(p -> ResponseEntity.ok(new ProductResponse(p)))
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID : " + id));
    }

    /**
     * Récupère un produit par son SKU
     */
    @GetMapping("/sku/{sku}")
    @Operation(summary = "Récupère un produit par SKU", description = "Retourne un produit spécifique par son SKU")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit trouvé"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<ProductResponse> getProductBySku(
            @Parameter(description = "SKU du produit à récupérer") @PathVariable String sku) {
        Optional<Product> product = productService.getProductBySku(sku);
        return product.map(p -> ResponseEntity.ok(new ProductResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crée un nouveau produit
     */
    @PostMapping
    @Operation(summary = "Crée un nouveau produit", description = "Ajoute un nouveau produit au catalogue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produit créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Produit déjà existant"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<ProductResponse> createProduct(
            @Parameter(description = "Données du produit à créer") @Valid @RequestBody ProductRequest productRequest) {
        try {
            Product product = new Product();
            product.setName(productRequest.getName());
            product.setDescription(productRequest.getDescription());
            product.setPrice(productRequest.getPrice());
            product.setStock(productRequest.getStock());
            product.setCategory(productRequest.getCategory());
            product.setSku(productRequest.getSku());
            product.setWeight(productRequest.getWeight());
            product.setDimensions(productRequest.getDimensions());

            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ProductResponse(createdProduct));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Met à jour un produit existant
     */
    @PutMapping("/{id}")
    @Operation(summary = "Met à jour un produit", description = "Modifie les informations d'un produit existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "ID du produit à mettre à jour") @PathVariable Long id,
            @Parameter(description = "Nouvelles données du produit") @Valid @RequestBody ProductRequest productRequest) {
        try {
            Product product = new Product();
            product.setName(productRequest.getName());
            product.setDescription(productRequest.getDescription());
            product.setPrice(productRequest.getPrice());
            product.setStock(productRequest.getStock());
            product.setCategory(productRequest.getCategory());
            product.setSku(productRequest.getSku());
            product.setWeight(productRequest.getWeight());
            product.setDimensions(productRequest.getDimensions());

            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(new ProductResponse(updatedProduct));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprime un produit
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime un produit", description = "Supprime un produit du catalogue (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Produit supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID du produit à supprimer") @PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Recherche des produits par catégorie
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "Recherche par catégorie", description = "Retourne les produits d'une catégorie spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des produits de la catégorie"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @Parameter(description = "Catégorie à rechercher") @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getProductsByCategory(category, pageable);
        Page<ProductResponse> response = products.map(ProductResponse::new);

        return ResponseEntity.ok(response);
    }

    /**
     * Recherche des produits par nom
     */
    @GetMapping("/search")
    @Operation(summary = "Recherche par nom", description = "Recherche des produits par nom (recherche partielle)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des produits correspondants"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Page<ProductResponse>> searchProductsByName(
            @Parameter(description = "Nom à rechercher") @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.searchProductsByName(name, pageable);
        Page<ProductResponse> response = products.map(ProductResponse::new);

        return ResponseEntity.ok(response);
    }

    /**
     * Récupère les produits disponibles
     */
    @GetMapping("/available")
    @Operation(summary = "Produits disponibles", description = "Retourne les produits en stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des produits disponibles"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Page<ProductResponse>> getAvailableProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getAvailableProducts(pageable);
        Page<ProductResponse> response = products.map(ProductResponse::new);

        return ResponseEntity.ok(response);
    }

    /**
     * Recherche des produits par plage de prix
     */
    @GetMapping("/price-range")
    @Operation(summary = "Recherche par prix", description = "Recherche des produits dans une plage de prix")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des produits dans la plage de prix"),
            @ApiResponse(responseCode = "400", description = "Paramètres de prix invalides"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Page<ProductResponse>> getProductsByPriceRange(
            @Parameter(description = "Prix minimum") @RequestParam BigDecimal minPrice,
            @Parameter(description = "Prix maximum") @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice, pageable);
        Page<ProductResponse> response = products.map(ProductResponse::new);

        return ResponseEntity.ok(response);
    }

    /**
     * Met à jour le stock d'un produit
     */
    @PatchMapping("/{id}/stock")
    @Operation(summary = "Met à jour le stock", description = "Modifie le stock d'un produit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Stock invalide"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<ProductResponse> updateStock(
            @Parameter(description = "ID du produit") @PathVariable Long id,
            @Parameter(description = "Nouveau stock") @RequestParam Integer stock) {
        try {
            Product updatedProduct = productService.updateStock(id, stock);
            return ResponseEntity.ok(new ProductResponse(updatedProduct));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Réserve du stock pour un produit
     */
    @PostMapping("/{id}/reserve")
    @Operation(summary = "Réserve du stock", description = "Réserve du stock pour un produit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock réservé avec succès"),
            @ApiResponse(responseCode = "400", description = "Quantité invalide ou stock insuffisant"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<ProductResponse> reserveStock(
            @Parameter(description = "ID du produit") @PathVariable Long id,
            @Parameter(description = "Quantité à réserver") @RequestParam Integer quantity) {
        try {
            Product updatedProduct = productService.reserveStock(id, quantity);
            return ResponseEntity.ok(new ProductResponse(updatedProduct));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Libère du stock réservé
     */
    @PostMapping("/{id}/release")
    @Operation(summary = "Libère du stock", description = "Libère du stock réservé pour un produit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock libéré avec succès"),
            @ApiResponse(responseCode = "400", description = "Quantité invalide"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<ProductResponse> releaseStock(
            @Parameter(description = "ID du produit") @PathVariable Long id,
            @Parameter(description = "Quantité à libérer") @RequestParam Integer quantity) {
        try {
            Product updatedProduct = productService.releaseStock(id, quantity);
            return ResponseEntity.ok(new ProductResponse(updatedProduct));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Vérifie la disponibilité d'un produit
     */
    @GetMapping("/{id}/availability")
    @Operation(summary = "Vérifie la disponibilité", description = "Vérifie si un produit est en stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut de disponibilité"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Boolean> checkAvailability(
            @Parameter(description = "ID du produit") @PathVariable Long id) {
        boolean available = productService.isProductAvailable(id);
        return ResponseEntity.ok(available);
    }

    /**
     * Vérifie si un produit peut être réservé
     */
    @GetMapping("/{id}/can-reserve")
    @Operation(summary = "Vérifie la réservation", description = "Vérifie si un produit peut être réservé")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut de réservation"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Boolean> canReserve(
            @Parameter(description = "ID du produit") @PathVariable Long id,
            @Parameter(description = "Quantité à réserver") @RequestParam Integer quantity) {
        boolean canReserve = productService.canReserveProduct(id, quantity);
        return ResponseEntity.ok(canReserve);
    }

    /**
     * Récupère les produits avec stock faible
     */
    @GetMapping("/low-stock")
    @Operation(summary = "Produits avec stock faible", description = "Retourne les produits avec stock faible")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des produits avec stock faible"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<ProductResponse>> getProductsWithLowStock(
            @Parameter(description = "Seuil de stock faible") @RequestParam(defaultValue = "10") Integer threshold) {
        List<Product> products = productService.getProductsWithLowStock(threshold);
        List<ProductResponse> response = products.stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
