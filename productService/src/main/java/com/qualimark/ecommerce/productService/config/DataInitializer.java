package com.qualimark.ecommerce.productService.config;

import com.qualimark.ecommerce.productService.model.Product;
import com.qualimark.ecommerce.productService.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Initialiseur de données pour démontrer les concepts des microservices
 * 
 * Cette classe charge des données d'exemple pour illustrer le fonctionnement
 * du ProductService dans le contexte d'un système de vente alimentaire.
 */
@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final ProductRepository productRepository;
    

    @Override
    public void run(String... args) throws Exception {
        // Vérifier si des données existent déjà
        if (productRepository.count() == 0) {
            initializeProducts();
            System.out.println("✅ Données d'exemple chargées avec succès !");
        } else {
            System.out.println("📦 Données existantes trouvées, pas de chargement nécessaire.");
        }
    }
    
    private void initializeProducts() {
        // Produits de la catégorie "Fruits"
        Product pomme = new Product(
            "Pomme Golden",
            "Pommes Golden délicieuses et croquantes",
            new BigDecimal("2.50"),
            100,
            "Fruits"
        );
        
        Product banane = new Product(
            "Banane Bio",
            "Bananes biologiques importées",
            new BigDecimal("3.20"),
            75,
            "Fruits"
        );
        
        Product orange = new Product(
            "Orange Navel",
            "Oranges Navel juteuses",
            new BigDecimal("2.80"),
            60,
            "Fruits"
        );
        
        // Produits de la catégorie "Légumes"
        Product carotte = new Product(
            "Carotte Bio",
            "Carottes biologiques fraîches",
            new BigDecimal("1.80"),
            120,
            "Légumes"
        );
        
        Product tomate = new Product(
            "Tomate Cerise",
            "Tomates cerises sucrées",
            new BigDecimal("4.50"),
            80,
            "Légumes"
        );
        
        Product salade = new Product(
            "Salade Laitue",
            "Salade laitue fraîche",
            new BigDecimal("1.20"),
            90,
            "Légumes"
        );
        
        // Produits de la catégorie "Épicerie"
        Product riz = new Product(
            "Riz Basmati",
            "Riz Basmati parfumé 1kg",
            new BigDecimal("3.50"),
            50,
            "Épicerie"
        );
        
        Product pates = new Product(
            "Pâtes Spaghetti",
            "Pâtes spaghetti de blé dur 500g",
            new BigDecimal("1.90"),
            70,
            "Épicerie"
        );
        
        Product huile = new Product(
            "Huile d'Olive Extra Vierge",
            "Huile d'olive extra vierge 500ml",
            new BigDecimal("8.90"),
            30,
            "Épicerie"
        );
        
        // Produits de la catégorie "Boulangerie"
        Product pain = new Product(
            "Pain de Campagne",
            "Pain de campagne artisanal",
            new BigDecimal("2.20"),
            25,
            "Boulangerie"
        );
        
        Product croissant = new Product(
            "Croissant au Beurre",
            "Croissants au beurre frais",
            new BigDecimal("1.50"),
            40,
            "Boulangerie"
        );
        
        // Sauvegarder tous les produits
        productRepository.save(pomme);
        productRepository.save(banane);
        productRepository.save(orange);
        productRepository.save(carotte);
        productRepository.save(tomate);
        productRepository.save(salade);
        productRepository.save(riz);
        productRepository.save(pates);
        productRepository.save(huile);
        productRepository.save(pain);
        productRepository.save(croissant);
        
        System.out.println("🛒 " + productRepository.count() + " produits ajoutés au catalogue");
    }
}
