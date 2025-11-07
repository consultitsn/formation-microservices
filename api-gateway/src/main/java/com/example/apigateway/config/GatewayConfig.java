package com.example.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Configuration globale pour l'API Gateway
 * 
 * Ce filtre ajoute des en-têtes personnalisés à toutes les requêtes
 * passant par le gateway pour le traçage et le monitoring.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
@Component
public class GatewayConfig implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GatewayConfig.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Ajouter un ID de trace unique pour chaque requête
        String traceId = UUID.randomUUID().toString();
        
        // Ajouter des en-têtes personnalisés
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-Trace-Id", traceId)
                .header("X-Gateway-Timestamp", LocalDateTime.now().toString())
                .header("X-Gateway-Request", "true")
                .build();
        
        // Logger la requête (optionnel)
        log.info(String.format(
            "[Gateway] %s %s - TraceId: %s",
            request.getMethod(),
            request.getURI(),
            traceId
        ));
        
        return chain.filter(exchange.mutate().request(request).build());
    }
    
    @Override
    public int getOrder() {
        // Ordre d'exécution du filtre (plus bas = exécuté plus tôt)
        return -1;
    }
}
