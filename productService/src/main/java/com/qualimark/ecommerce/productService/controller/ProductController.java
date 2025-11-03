package com.qualimark.ecommerce.productService.controller;

import com.qualimark.ecommerce.productService.model.Product;
import com.qualimark.ecommerce.productService.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Récupère tous les produits
     */
    @GetMapping
    @Operation(summary = "Récupère tous les produits", description = "Retourne la liste de tous les produits disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des produits récupérée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
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
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "ID du produit à récupérer") @PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
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
    public ResponseEntity<Product> createProduct(
            @Parameter(description = "Données du produit à créer") @Valid @RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
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
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "ID du produit à mettre à jour") @PathVariable Long id,
            @Parameter(description = "Nouvelles données du produit") @Valid @RequestBody Product productDetails) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDetails);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprime un produit
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime un produit", description = "Supprime un produit du catalogue")
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
    public ResponseEntity<List<Product>> getProductsByCategory(
            @Parameter(description = "Catégorie à rechercher") @PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
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
    public ResponseEntity<List<Product>> searchProductsByName(
            @Parameter(description = "Nom à rechercher") @RequestParam String name) {
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
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
    public ResponseEntity<List<Product>> getAvailableProducts() {
        List<Product> products = productService.getAvailableProducts();
        return ResponseEntity.ok(products);
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
    public ResponseEntity<Product> updateStock(
            @Parameter(description = "ID du produit") @PathVariable Long id,
            @Parameter(description = "Nouveau stock") @RequestParam Integer stock) {
        try {
            Product updatedProduct = productService.updateStock(id, stock);
            return ResponseEntity.ok(updatedProduct);
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
}
