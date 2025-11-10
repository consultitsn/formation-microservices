package com.example.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Application principale du Order Service
 * 
 * Ce service illustre la communication entre microservices avec :
 * - Feign Client pour la communication REST
 * - Circuit Breaker avec Resilience4j
 * - Sécurité OAuth2 avec Keycloak
 * - Gestion des événements asynchrones
 * - Pattern Saga pour les transactions distribuées
 * 
 * @author Formation Microservices
 * @version 1.0
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableFeignClients
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
        
        System.out.println("🚀 Order Service démarré sur le port 8082 !");
        System.out.println("📚 Documentation API : http://localhost:8082/swagger-ui.html");
        System.out.println("📊 Métriques Prometheus : http://localhost:8082/actuator/prometheus");
        System.out.println("🔍 Health Check : http://localhost:8082/actuator/health");
        System.out.println("⚡ Circuit Breakers : http://localhost:8082/actuator/circuitbreakers");
    }
}
