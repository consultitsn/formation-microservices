-- Migration Flyway pour créer la table order_items
-- Version: 2
-- Description: Création de la table des articles de commande

CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(10, 2) NOT NULL,
    total_price NUMERIC(10, 2) NOT NULL,
    notes VARCHAR(500),
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT chk_quantity_min CHECK (quantity >= 1),
    CONSTRAINT chk_quantity_max CHECK (quantity <= 1000),
    CONSTRAINT chk_unit_price_positive CHECK (unit_price >= 0.01),
    CONSTRAINT chk_unit_price_max CHECK (unit_price <= 999999.99),
    CONSTRAINT chk_total_price_positive CHECK (total_price >= 0.01),
    CONSTRAINT chk_total_price_max CHECK (total_price <= 999999.99)
);

-- Index pour améliorer les performances des requêtes fréquentes
CREATE INDEX IF NOT EXISTS idx_order_item_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_item_product_id ON order_items(product_id);

-- Commentaires pour la documentation
COMMENT ON TABLE order_items IS 'Table des articles contenus dans une commande';
COMMENT ON COLUMN order_items.id IS 'Identifiant unique de l''article de commande';
COMMENT ON COLUMN order_items.order_id IS 'Référence à la commande parente';
COMMENT ON COLUMN order_items.product_id IS 'Identifiant du produit (référence externe au product-service)';
COMMENT ON COLUMN order_items.product_name IS 'Nom du produit au moment de la commande (snapshot)';
COMMENT ON COLUMN order_items.quantity IS 'Quantité commandée';
COMMENT ON COLUMN order_items.unit_price IS 'Prix unitaire au moment de la commande (snapshot)';
COMMENT ON COLUMN order_items.total_price IS 'Prix total de l''article (quantity * unit_price)';
COMMENT ON COLUMN order_items.notes IS 'Notes optionnelles sur l''article';

