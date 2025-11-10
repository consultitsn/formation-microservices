# 🗄️ Migrations Flyway - Order Service

## 📋 Description

Ce répertoire contient les scripts de migration Flyway pour la base de données PostgreSQL du service de commandes.

## 📁 Structure des Migrations

### V1__Create_commandes_table.sql
**Version:** 1  
**Description:** Création de la table principale `orders`

**Tables créées:**
- `orders` : Table principale des commandes

**Colonnes:**
- `id` : Identifiant unique (BIGSERIAL, PRIMARY KEY)
- `customer_id` : Identifiant du client (VARCHAR(100), NOT NULL)
- `status` : Statut de la commande (VARCHAR(20), NOT NULL)
- `total_amount` : Montant total (NUMERIC(10,2), NOT NULL)
- `notes` : Notes optionnelles (VARCHAR(500))
- `cancellation_reason` : Raison d'annulation (VARCHAR(500))
- `created_at` : Date de création (TIMESTAMP, NOT NULL, DEFAULT CURRENT_TIMESTAMP)
- `updated_at` : Date de mise à jour (TIMESTAMP)
- `version` : Version pour optimistic locking (BIGINT, NOT NULL, DEFAULT 0)

**Index créés:**
- `idx_order_customer_id` : Sur `customer_id`
- `idx_order_status` : Sur `status`
- `idx_order_created_at` : Sur `created_at`

**Contraintes:**
- `chk_total_amount_positive` : Vérifie que le montant >= 0.01
- `chk_total_amount_max` : Vérifie que le montant <= 999999.99
- `chk_status_valid` : Vérifie que le statut est valide

**Statuts valides:**
- `PENDING` : En attente
- `CONFIRMED` : Confirmée
- `PREPARING` : En préparation
- `READY_FOR_DELIVERY` : Prête pour livraison
- `IN_DELIVERY` : En livraison
- `DELIVERED` : Livrée
- `CANCELLED` : Annulée
- `PENDING_CANCELLATION` : En attente d'annulation
- `FAILED` : Échouée

---

### V2__Create_order_items_table.sql
**Version:** 2  
**Description:** Création de la table `order_items` pour les articles de commande

**Tables créées:**
- `order_items` : Table des articles contenus dans une commande

**Colonnes:**
- `id` : Identifiant unique (BIGSERIAL, PRIMARY KEY)
- `order_id` : Référence à la commande (BIGINT, NOT NULL, FOREIGN KEY)
- `product_id` : Identifiant du produit (BIGINT, NOT NULL)
- `product_name` : Nom du produit (VARCHAR(200), NOT NULL)
- `quantity` : Quantité commandée (INTEGER, NOT NULL)
- `unit_price` : Prix unitaire (NUMERIC(10,2), NOT NULL)
- `total_price` : Prix total (NUMERIC(10,2), NOT NULL)
- `notes` : Notes optionnelles (VARCHAR(500))

**Index créés:**
- `idx_order_item_order_id` : Sur `order_id`
- `idx_order_item_product_id` : Sur `product_id`

**Contraintes:**
- `fk_order_item_order` : Clé étrangère vers `orders(id)` avec CASCADE DELETE
- `chk_quantity_min` : Vérifie que la quantité >= 1
- `chk_quantity_max` : Vérifie que la quantité <= 1000
- `chk_unit_price_positive` : Vérifie que le prix unitaire >= 0.01
- `chk_unit_price_max` : Vérifie que le prix unitaire <= 999999.99
- `chk_total_price_positive` : Vérifie que le prix total >= 0.01
- `chk_total_price_max` : Vérifie que le prix total <= 999999.99

---

### V3__Add_updated_at_trigger.sql
**Version:** 3  
**Description:** Ajout d'un trigger pour mettre à jour automatiquement `updated_at`

**Fonctions créées:**
- `update_updated_at_column()` : Fonction trigger pour mettre à jour `updated_at`

**Triggers créés:**
- `update_orders_updated_at` : Déclenché avant chaque UPDATE sur `orders`

**Comportement:**
- Met automatiquement à jour le champ `updated_at` avec `CURRENT_TIMESTAMP` lors de chaque modification d'une commande

---

## 🚀 Utilisation

### Exécution automatique
Les migrations sont exécutées automatiquement au démarrage de l'application grâce à la configuration Flyway dans `application.yml`:

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true
```

### Exécution manuelle
Pour exécuter les migrations manuellement :

```bash
# Via Maven (si le plugin Flyway est configuré)
mvn flyway:migrate

# Via Flyway CLI
flyway migrate
```

### Vérification de l'état
Pour vérifier l'état des migrations :

```bash
# Via Maven
mvn flyway:info

# Via Flyway CLI
flyway info
```

## 📝 Conventions de Nommage

Les fichiers de migration suivent la convention Flyway :
- Format : `V{version}__{Description}.sql`
- Version : Numéro séquentiel (1, 2, 3, ...)
- Description : Description en anglais avec underscores

Exemples :
- `V1__Create_commandes_table.sql`
- `V2__Create_order_items_table.sql`
- `V3__Add_updated_at_trigger.sql`

## ⚠️ Bonnes Pratiques

1. **Ne jamais modifier une migration existante** : Une fois exécutée, une migration ne doit jamais être modifiée. Créez une nouvelle migration pour les modifications.

2. **Transactions** : Flyway exécute chaque migration dans une transaction. En cas d'erreur, la transaction est rollback.

3. **Idempotence** : Utilisez `IF NOT EXISTS` et `IF EXISTS` pour rendre les migrations idempotentes quand c'est possible.

4. **Tests** : Testez toujours les migrations sur un environnement de développement avant de les déployer en production.

5. **Backup** : Faites toujours un backup de la base de données avant d'exécuter des migrations en production.

## 🔍 Vérification

Pour vérifier que les migrations ont été appliquées correctement :

```sql
-- Vérifier les tables créées
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
  AND table_name IN ('orders', 'order_items');

-- Vérifier les index
SELECT indexname, tablename 
FROM pg_indexes 
WHERE schemaname = 'public' 
  AND tablename IN ('orders', 'order_items');

-- Vérifier les contraintes
SELECT constraint_name, table_name, constraint_type
FROM information_schema.table_constraints
WHERE table_schema = 'public'
  AND table_name IN ('orders', 'order_items');
```

## 📚 Ressources

- [Documentation Flyway](https://flywaydb.org/documentation/)
- [Flyway avec Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)

