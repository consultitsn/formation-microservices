-- Migration Flyway pour créer la table details_commande
-- Version: 2
-- Description: Création de la table des articles de commande

CREATE TABLE IF NOT EXISTS details_commande (
    id BIGSERIAL PRIMARY KEY,
    commande_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(10, 2) NOT NULL,
    total_price NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_commande_item_commande FOREIGN KEY (commande_id) REFERENCES commandes(id) ON DELETE CASCADE,
    CONSTRAINT chk_quantity_min CHECK (quantity >= 1),
    CONSTRAINT chk_quantity_max CHECK (quantity <= 1000),
    CONSTRAINT chk_unit_price_positive CHECK (unit_price >= 0.01),
    CONSTRAINT chk_unit_price_max CHECK (unit_price <= 999999.99),
    CONSTRAINT chk_total_price_positive CHECK (total_price >= 0.01),
    CONSTRAINT chk_total_price_max CHECK (total_price <= 999999.99)
);

-- Index pour améliorer les performances des requêtes fréquentes
CREATE INDEX IF NOT EXISTS idx_commande_item_commande_id ON details_commande(commande_id);
CREATE INDEX IF NOT EXISTS idx_commande_item_product_id ON details_commande(product_id);

-- Commentaires pour la documentation
COMMENT ON TABLE details_commande IS 'Table des articles contenus dans une commande';
COMMENT ON COLUMN details_commande.id IS 'Identifiant unique de l''article de commande';
COMMENT ON COLUMN details_commande.commande_id IS 'Référence à la commande parente';
COMMENT ON COLUMN details_commande.product_id IS 'Identifiant du produit (référence externe au product-service)';
COMMENT ON COLUMN details_commande.product_name IS 'Nom du produit au moment de la commande (snapshot)';
COMMENT ON COLUMN details_commande.quantity IS 'Quantité commandée';
COMMENT ON COLUMN details_commande.unit_price IS 'Prix unitaire au moment de la commande (snapshot)';
COMMENT ON COLUMN details_commande.total_price IS 'Prix total de l''article (quantity * unit_price)';

