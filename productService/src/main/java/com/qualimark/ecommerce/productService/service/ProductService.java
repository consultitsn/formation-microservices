package com.qualimark.ecommerce.productService.service;

import com.qualimark.ecommerce.productService.model.Product;
import com.qualimark.ecommerce.productService.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Récupère tous les produits
     *
     * @return Liste de tous les produits
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Récupère un produit par son ID
     *
     * @param id L'ID du produit
     * @return Le produit s'il existe
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Crée un nouveau produit
     *
     * @param product Le produit à créer
     * @return Le produit créé
     */
    public Product createProduct(Product product) {
        // Vérification que le produit n'existe pas déjà
        if (productRepository.findByName(product.getName()).isPresent()) {
            throw new IllegalArgumentException("Un produit avec ce nom existe déjà");
        }

        return productRepository.save(product);
    }

    /**
     * Met à jour un produit existant
     *
     * @param id             L'ID du produit à mettre à jour
     * @param productDetails Les nouvelles informations du produit
     * @return Le produit mis à jour
     */
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé avec l'ID : " + id));

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        product.setCategory(productDetails.getCategory());

        return productRepository.save(product);
    }

    /**
     * Supprime un produit
     *
     * @param id L'ID du produit à supprimer
     */
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Produit non trouvé avec l'ID : " + id);
        }

        productRepository.deleteById(id);
    }

    /**
     * Recherche des produits par catégorie
     *
     * @param category La catégorie à rechercher
     * @return Liste des produits de la catégorie
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    /**
     * Recherche des produits par nom
     *
     * @param name Le nom à rechercher
     * @return Liste des produits correspondants
     */
    @Transactional(readOnly = true)
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Récupère les produits disponibles (en stock)
     *
     * @return Liste des produits en stock
     */
    @Transactional(readOnly = true)
    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }

    /**
     * Met à jour le stock d'un produit
     *
     * @param id       L'ID du produit
     * @param newStock Le nouveau stock
     * @return Le produit mis à jour
     */
    public Product updateStock(Long id, Integer newStock) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé avec l'ID : " + id));

        if (newStock < 0) {
            throw new IllegalArgumentException("Le stock ne peut pas être négatif");
        }

        product.setStock(newStock);
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
                .map(product -> product.getStock() > 0)
                .orElse(false);
    }

}
