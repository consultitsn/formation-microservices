-- Migration Flyway pour créer la table commandes
-- Version: 1
-- Description: Création de la table principale des commandes

CREATE TABLE IF NOT EXISTS commandes (
    id BIGSERIAL PRIMARY KEY,
    customer_id VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT chk_total_amount_positive CHECK (total_amount >= 0.01),
    CONSTRAINT chk_total_amount_max CHECK (total_amount <= 999999.99),
    CONSTRAINT chk_status_valid CHECK (status IN ('PENDING', 'CONFIRMED', 'PREPARING', 'READY_FOR_DELIVERY', 'IN_DELIVERY', 'DELIVERED', 'CANCELLED', 'PENDING_CANCELLATION', 'FAILED'))
);

-- Index pour améliorer les performances des requêtes fréquentes
CREATE INDEX IF NOT EXISTS idx_order_customer_id ON commandes(customer_id);
CREATE INDEX IF NOT EXISTS idx_order_status ON commandes(status);
CREATE INDEX IF NOT EXISTS idx_order_created_at ON commandes(created_at);

-- Commentaires pour la documentation
COMMENT ON TABLE commandes IS 'Table principale des commandes';
COMMENT ON COLUMN commandes.id IS 'Identifiant unique de la commande';
COMMENT ON COLUMN commandes.customer_id IS 'Identifiant du client (référence externe)';
COMMENT ON COLUMN commandes.status IS 'Statut de la commande (PENDING, CONFIRMED, PREPARING, READY_FOR_DELIVERY, IN_DELIVERY, DELIVERED, CANCELLED, PENDING_CANCELLATION, FAILED)';
COMMENT ON COLUMN commandes.total_amount IS 'Montant total de la commande';
COMMENT ON COLUMN commandes.created_at IS 'Date et heure de création de la commande';
COMMENT ON COLUMN commandes.updated_at IS 'Date et heure de dernière mise à jour';

