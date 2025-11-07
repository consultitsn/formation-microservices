# 🔍 Service Discovery (Eureka) - Jour 4

## 📋 Description

Le Service Discovery est un registre centralisé qui permet aux microservices de s'enregistrer et de découvrir automatiquement les autres services sans avoir besoin de connaître leurs adresses IP ou ports exacts.

**Eureka** est le composant Netflix qui implémente le pattern de Service Discovery dans l'écosystème Spring Cloud.

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│              Service Discovery (Eureka)                      │
│                    Port 8761                                │
│  ┌───────────────────────────────────────────────────────┐ │
│  │  Registre des Services:                                │ │
│  │  - product-service (8081)                              │ │
│  │  - order-service (8082)                                │ │
│  │  - api-gateway (8080)                                   │ │
│  └───────────────────────────────────────────────────────┘ │
└─────────────────┬───────────────────────────────────────────┘
                  │
      ┌───────────┴───────────┐
      │                       │
┌─────▼─────┐         ┌───────▼─────┐
│ Product   │         │ Order       │
│ Service   │◄────────┤ Service     │
│ (8081)    │         │ (8082)      │
└─────┬─────┘         └─────────────┘
      │
      │  ┌─────────────▼─────────────┐
      │  │     API Gateway           │
      │  │  (Découvre via Eureka)    │
      │  └───────────────────────────┘
```

## 🚀 Démarrage

### Prérequis

- Java 17+
- Maven 3.6+

### Compilation

```bash
cd service-discovery
mvn clean install
```

### Exécution

```bash
mvn spring-boot:run
```

Le Service Discovery sera accessible sur : `http://localhost:8761`

## 📊 Dashboard Eureka

Une fois démarré, accédez au dashboard Eureka pour visualiser les services enregistrés :

**URL** : `http://localhost:8761`

Le dashboard affiche :
- **Instances currently registered with Eureka** : Liste des services enregistrés
- **General Info** : Informations sur le serveur Eureka
- **DS Replicas** : Répliques du serveur (pour mode haute disponibilité)

## 🔧 Configuration

### Mode Standalone (Développement)

Le service est configuré en mode standalone pour le développement :

```yaml
eureka:
  client:
    register-with-eureka: false  # Ne pas s'enregistrer auprès d'autres instances
    fetch-registry: false       # Ne pas récupérer le registre d'autres instances
  server:
    enable-self-preservation: false  # Désactiver la protection (dev uniquement)
```

### Mode Production (Haute Disponibilité)

Pour la production, configurez plusieurs instances Eureka :

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://eureka1:8761/eureka/,http://eureka2:8761/eureka/
```

## 📡 Services Enregistrés

Les services suivants s'enregistrent automatiquement auprès d'Eureka :

1. **api-gateway** (Port 8080)
   - Point d'entrée unique pour tous les microservices
   - Utilise Eureka pour découvrir les services

2. **product-service** (Port 8081)
   - Service de gestion des produits
   - S'enregistre auprès d'Eureka au démarrage

3. **order-service** (Port 8082)
   - Service de gestion des commandes
   - Utilise Eureka pour découvrir product-service

## 🔄 Fonctionnement

### Enregistrement des Services

1. Au démarrage, chaque service s'enregistre auprès d'Eureka avec :
   - Son nom (spring.application.name)
   - Son adresse IP et port
   - Son statut de santé

2. Eureka maintient un registre des services disponibles

3. Les services renouvellent leur enregistrement toutes les 30 secondes

### Découverte des Services

1. Les services clients interrogent Eureka pour découvrir les services

2. Eureka retourne la liste des instances disponibles

3. Le client utilise un load balancer pour choisir une instance

4. Les requêtes sont routées vers l'instance choisie

### Health Checks

- Eureka vérifie périodiquement la santé des services
- Les services indisponibles sont retirés du registre
- Les services peuvent signaler leur statut via l'endpoint `/actuator/health`

## 📊 Monitoring

### Endpoints Actuator

- **Health Check** : `http://localhost:8761/actuator/health`
- **Métriques Prometheus** : `http://localhost:8761/actuator/prometheus`
- **Info** : `http://localhost:8761/actuator/info`

### Métriques Disponibles

- Nombre de services enregistrés
- Nombre de requêtes de découverte
- Taux de renouvellement des enregistrements
- Services expirés

## 🔐 Sécurité

Pour la production, ajoutez :

1. **Authentification** : Protéger l'accès au dashboard Eureka
2. **HTTPS** : Utiliser HTTPS pour les communications
3. **Firewall** : Restreindre l'accès au port 8761

## 🐛 Dépannage

### Le service ne s'enregistre pas

1. Vérifier que Eureka est démarré
2. Vérifier la configuration `eureka.client.service-url.defaultZone`
3. Vérifier les logs du service pour les erreurs de connexion

### Les services ne se découvrent pas

1. Vérifier que les services sont enregistrés dans le dashboard Eureka
2. Vérifier que `fetch-registry: true` dans la configuration
3. Attendre quelques secondes pour la synchronisation

### Services disparaissent du registre

1. Vérifier la santé des services (`/actuator/health`)
2. Vérifier les paramètres de renouvellement
3. Vérifier les logs Eureka pour les expirations

## 📚 Ressources

- [Spring Cloud Netflix Eureka](https://spring.io/projects/spring-cloud-netflix)
- [Eureka Documentation](https://github.com/Netflix/eureka/wiki)
- [Service Discovery Pattern](https://microservices.io/patterns/service-registry.html)

## 🎯 Avantages

1. **Découplage** : Les services n'ont plus besoin de connaître les adresses IP
2. **Scalabilité** : Facilite l'ajout de nouvelles instances
3. **Load Balancing** : Répartition automatique de la charge
4. **Résilience** : Détection automatique des services indisponibles
5. **Flexibilité** : Les services peuvent changer d'adresse sans impact
