-- Migration pour créer la table products
-- Version: 1
-- Description: Création de la table products avec tous les champs nécessaires

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    stock INTEGER NOT NULL CHECK (stock >= 0),
    category VARCHAR(50) NOT NULL,
    sku VARCHAR(50) UNIQUE,
    weight DECIMAL(10,3),
    dimensions VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- Index pour améliorer les performances
CREATE INDEX idx_product_category ON products(category);
CREATE INDEX idx_product_name ON products(name);
CREATE INDEX idx_product_sku ON products(sku);
CREATE INDEX idx_product_created_at ON products(created_at);
CREATE INDEX idx_product_is_active ON products(is_active);
CREATE INDEX idx_product_stock ON products(stock);

-- Trigger pour mettre à jour updated_at automatiquement
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_products_updated_at 
    BEFORE UPDATE ON products 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Commentaires sur la table et les colonnes
COMMENT ON TABLE products IS 'Table des produits du catalogue';
COMMENT ON COLUMN products.id IS 'Identifiant unique du produit';
COMMENT ON COLUMN products.name IS 'Nom du produit';
COMMENT ON COLUMN products.description IS 'Description détaillée du produit';
COMMENT ON COLUMN products.price IS 'Prix du produit en euros';
COMMENT ON COLUMN products.stock IS 'Quantité en stock';
COMMENT ON COLUMN products.category IS 'Catégorie du produit';
COMMENT ON COLUMN products.sku IS 'Code SKU unique du produit';
COMMENT ON COLUMN products.weight IS 'Poids du produit en kg';
COMMENT ON COLUMN products.dimensions IS 'Dimensions du produit (L x l x H)';
COMMENT ON COLUMN products.is_active IS 'Indique si le produit est actif';
COMMENT ON COLUMN products.created_at IS 'Date de création du produit';
COMMENT ON COLUMN products.updated_at IS 'Date de dernière modification';
COMMENT ON COLUMN products.version IS 'Version pour le contrôle d''accès concurrentiel';
