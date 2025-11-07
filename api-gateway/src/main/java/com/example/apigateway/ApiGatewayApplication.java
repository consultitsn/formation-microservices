package com.example.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application principale de l'API Gateway
 * 
 * Ce service agit comme point d'entrée unique pour tous les microservices.
 * Il route les requêtes vers les services appropriés en utilisant Spring Cloud Gateway.
 * 
 * Fonctionnalités :
 * - Routage des requêtes vers les microservices
 * - Circuit Breaker pour la résilience
 * - Rate limiting
 * - Monitoring et métriques
 * - Documentation API centralisée
 * 
 * @author Formation Microservices
 * @version 1.0
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
        
        System.out.println("🚀 API Gateway démarré sur le port 8080 !");
        System.out.println("📚 Documentation API : http://localhost:8080/swagger-ui.html");
        System.out.println("📊 Métriques Prometheus : http://localhost:8080/actuator/prometheus");
        System.out.println("🔍 Health Check : http://localhost:8080/actuator/health");
        System.out.println("📦 Product Service : http://localhost:8080/api/v1/products");
        System.out.println("📦 Order Service : http://localhost:8080/api/v1/orders");
    }
}
