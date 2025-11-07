# 🚪 API Gateway - Jour 4

## 📋 Description

L'API Gateway est le point d'entrée unique pour tous les microservices. Il route les requêtes vers les services appropriés et fournit des fonctionnalités centralisées comme :

- **Routage** : Route les requêtes vers les microservices appropriés
- **Circuit Breaker** : Protège contre les pannes en cascade
- **Retry** : Réessaie automatiquement les requêtes échouées
- **Monitoring** : Métriques et santé des services
- **CORS** : Gestion des requêtes cross-origin
- **Traçage** : Ajout d'IDs de trace pour le debugging

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│              Service Discovery (Eureka)                      │
│                    Port 8761                                 │
└─────────────────┬───────────────────────────────────────────┘
                  │
┌─────────────────▼─────────────────────────────────────────────┐
│                    API Gateway (Port 8080)                  │
│  ┌───────────────────────────────────────────────────────┐ │
│  │  Routes (via Eureka):                                  │ │
│  │  - /api/v1/products/** → lb://product-service         │ │
│  │  - /api/v1/orders/** → lb://order-service             │ │
│  └───────────────────────────────────────────────────────┘ │
└─────────────────┬───────────────────────────────────────────┘
                  │
      ┌───────────┴───────────┐
      │                       │
┌─────▼─────┐         ┌───────▼─────┐
│ Product   │         │ Order       │
│ Service   │         │ Service     │
│ (8081)    │         │ (8082)      │
└───────────┘         └─────────────┘
```

**Note** : L'API Gateway utilise maintenant **Eureka** pour découvrir automatiquement les services. Les routes utilisent `lb://service-name` au lieu d'URLs hardcodées, permettant le load balancing automatique.

## 🚀 Démarrage

### Prérequis

- Java 17+
- Maven 3.6+
- **Service Discovery (Eureka)** doit être démarré en premier
- Les services Product Service et Order Service doivent être démarrés (ils s'enregistrent automatiquement auprès d'Eureka)

### Compilation

```bash
cd api-gateway
mvn clean install
```

### Exécution

```bash
mvn spring-boot:run
```

L'API Gateway sera accessible sur : `http://localhost:8080`

## 📡 Routes Configurées

### Product Service

- **Route** : `/api/v1/products/**`
- **Service Backend** : `lb://product-service` (découvert via Eureka)
- **Méthodes** : GET, POST, PUT, DELETE, PATCH
- **Load Balancing** : Automatique via Spring Cloud LoadBalancer

**Exemples :**
- `GET http://localhost:8080/api/v1/products` - Liste des produits
- `GET http://localhost:8080/api/v1/products/1` - Détails d'un produit
- `POST http://localhost:8080/api/v1/products` - Créer un produit

### Order Service

- **Route** : `/api/v1/orders/**`
- **Service Backend** : `lb://order-service` (découvert via Eureka)
- **Méthodes** : GET, POST, PUT, DELETE, PATCH
- **Load Balancing** : Automatique via Spring Cloud LoadBalancer

**Exemples :**
- `GET http://localhost:8080/api/v1/orders` - Liste des commandes
- `GET http://localhost:8080/api/v1/orders/1` - Détails d'une commande
- `POST http://localhost:8080/api/v1/orders` - Créer une commande

## 🔧 Configuration

### Service Discovery (Eureka)

L'API Gateway s'enregistre auprès d'Eureka et découvre les services automatiquement :

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true
```

Les routes utilisent le format `lb://service-name` pour la découverte automatique et le load balancing.

### Circuit Breaker

Le Circuit Breaker protège les services contre les pannes en cascade :

- **Seuil d'échec** : 50% des requêtes
- **Fenêtre glissante** : 10 requêtes
- **Durée d'attente** : 5 secondes
- **Fallback** : Retourne une réponse d'erreur standardisée

### Retry

Les requêtes échouées sont automatiquement réessayées :

- **Nombre de tentatives** : 3
- **Délai initial** : 50ms
- **Backoff exponentiel** : x2
- **Codes HTTP retry** : 502, 500

### Filtres Globaux

Toutes les requêtes passent par des filtres qui ajoutent :

- `X-Gateway-Request: true` : Indique que la requête passe par le gateway
- `X-Trace-Id: <uuid>` : ID unique pour le traçage
- `X-Gateway-Timestamp: <timestamp>` : Horodatage de la requête

## 📊 Monitoring

### Endpoints Actuator

- **Health Check** : `http://localhost:8080/actuator/health`
- **Métriques Prometheus** : `http://localhost:8080/actuator/prometheus`
- **Routes Gateway** : `http://localhost:8080/actuator/gateway/routes`
- **Circuit Breakers** : `http://localhost:8080/actuator/health`

### Documentation API

- **Swagger UI** : `http://localhost:8080/swagger-ui.html`
- **API Docs** : `http://localhost:8080/api-docs`

## 🔄 Fallback

En cas de panne d'un service, le Circuit Breaker active le fallback :

- **Product Service Fallback** : `http://localhost:8080/fallback/product-service`
- **Order Service Fallback** : `http://localhost:8080/fallback/order-service`

Les réponses de fallback retournent un statut `503 Service Unavailable` avec un message d'erreur explicite.

## 🧪 Tests

### Test de Routage

```bash
# Test Product Service via Gateway
curl http://localhost:8080/api/v1/products

# Test Order Service via Gateway
curl http://localhost:8080/api/v1/orders
```

### Test de Circuit Breaker

1. Démarrer le Gateway
2. Arrêter le Product Service
3. Faire plusieurs requêtes vers `/api/v1/products`
4. Le Circuit Breaker s'ouvrira après 5 échecs
5. Les requêtes suivantes retourneront le fallback

### Test de Retry

```bash
# Simuler une erreur temporaire
# Le Gateway réessayera automatiquement 3 fois
curl http://localhost:8080/api/v1/products
```

## 📝 Logs

Les logs incluent :

- **Requêtes entrantes** : Méthode, URI, Trace ID
- **Routage** : Route utilisée, Service cible
- **Circuit Breaker** : Événements (OPEN, CLOSED, HALF_OPEN)
- **Erreurs** : Exceptions et stack traces

## 🔐 Sécurité (À venir)

Les fonctionnalités de sécurité suivantes peuvent être ajoutées :

- Authentification OAuth2/JWT
- Rate Limiting par client
- IP Whitelisting
- Request/Response Transformation

## 🐛 Dépannage

### Le Gateway ne démarre pas

1. Vérifier que le port 8080 n'est pas utilisé
2. Vérifier les dépendances Maven
3. Vérifier les logs pour les erreurs

### Les routes ne fonctionnent pas

1. Vérifier que les services backend sont démarrés
2. Vérifier les URLs dans `application.yml`
3. Vérifier les logs du Gateway pour les erreurs de routage

### Circuit Breaker toujours ouvert

1. Vérifier la santé des services backend
2. Vérifier la configuration du Circuit Breaker
3. Consulter les métriques via `/actuator/prometheus`

## 📚 Ressources

- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)
- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

## 🎯 Prochaines Étapes

- Ajouter la découverte de services (Eureka/Consul)
- Implémenter l'authentification OAuth2
- Ajouter le rate limiting avec Redis
- Implémenter la transformation de requêtes/réponses
- Ajouter le logging centralisé (ELK Stack)
