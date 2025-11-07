package com.example.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Application principale du Service Discovery (Eureka Server)
 * 
 * Ce service agit comme registre de services pour tous les microservices.
 * Les services s'enregistrent auprès d'Eureka et découvrent les autres services
 * via leur nom au lieu d'utiliser des URLs hardcodées.
 * 
 * Fonctionnalités :
 * - Registre de services centralisé
 * - Découverte automatique des services
 * - Health checks des services enregistrés
 * - Dashboard web pour visualiser les services
 * 
 * @author Formation Microservices
 * @version 1.0
 */
@SpringBootApplication
@EnableEurekaServer
public class ServiceDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceDiscoveryApplication.class, args);
        
        System.out.println("🚀 Service Discovery (Eureka) démarré sur le port 8761 !");
        System.out.println("📊 Dashboard Eureka : http://localhost:8761");
        System.out.println("🔍 Health Check : http://localhost:8761/actuator/health");
        System.out.println("📈 Métriques Prometheus : http://localhost:8761/actuator/prometheus");
    }
}
