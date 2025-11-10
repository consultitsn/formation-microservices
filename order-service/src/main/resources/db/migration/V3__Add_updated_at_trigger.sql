-- Migration Flyway pour ajouter un trigger de mise à jour automatique
-- Version: 3
-- Description: Trigger pour mettre à jour automatiquement updated_at lors des modifications

-- Fonction pour mettre à jour automatiquement le champ updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger pour la table orders
CREATE TRIGGER update_orders_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Commentaire
COMMENT ON FUNCTION update_updated_at_column() IS 'Fonction trigger pour mettre à jour automatiquement le champ updated_at';

