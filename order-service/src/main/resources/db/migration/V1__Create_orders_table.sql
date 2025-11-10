-- Migration Flyway pour créer la table orders
-- Version: 1
-- Description: Création de la table principale des commandes

CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    customer_id VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    notes VARCHAR(500),
    cancellation_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_total_amount_positive CHECK (total_amount >= 0.01),
    CONSTRAINT chk_total_amount_max CHECK (total_amount <= 999999.99),
    CONSTRAINT chk_status_valid CHECK (status IN ('PENDING', 'CONFIRMED', 'PREPARING', 'READY_FOR_DELIVERY', 'IN_DELIVERY', 'DELIVERED', 'CANCELLED', 'PENDING_CANCELLATION', 'FAILED'))
);

-- Index pour améliorer les performances des requêtes fréquentes
CREATE INDEX IF NOT EXISTS idx_order_customer_id ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_order_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_order_created_at ON orders(created_at);

-- Commentaires pour la documentation
COMMENT ON TABLE orders IS 'Table principale des commandes';
COMMENT ON COLUMN orders.id IS 'Identifiant unique de la commande';
COMMENT ON COLUMN orders.customer_id IS 'Identifiant du client (référence externe)';
COMMENT ON COLUMN orders.status IS 'Statut de la commande (PENDING, CONFIRMED, PREPARING, READY_FOR_DELIVERY, IN_DELIVERY, DELIVERED, CANCELLED, PENDING_CANCELLATION, FAILED)';
COMMENT ON COLUMN orders.total_amount IS 'Montant total de la commande';
COMMENT ON COLUMN orders.notes IS 'Notes optionnelles sur la commande';
COMMENT ON COLUMN orders.cancellation_reason IS 'Raison de l''annulation si applicable';
COMMENT ON COLUMN orders.created_at IS 'Date et heure de création de la commande';
COMMENT ON COLUMN orders.updated_at IS 'Date et heure de dernière mise à jour';
COMMENT ON COLUMN orders.version IS 'Version pour le contrôle d''accès concurrentiel optimiste (Optimistic Locking)';

