-- Migration pour insérer des données d'exemple
-- Version: 2
-- Description: Insertion de produits d'exemple pour les tests et démonstrations

-- Produits de la catégorie "Fruits"
INSERT INTO products (name, description, price, stock, category, sku, weight, dimensions, is_active) VALUES
('Pomme Golden', 'Pommes Golden délicieuses et croquantes, parfaites pour les desserts', 2.50, 100, 'Fruits', 'FRUIT-001', 0.200, '6-8cm', true),
('Banane Bio', 'Bananes biologiques importées, riches en potassium', 3.20, 75, 'Fruits', 'FRUIT-002', 0.150, '15-20cm', true),
('Orange Navel', 'Oranges Navel juteuses, idéales pour les jus', 2.80, 60, 'Fruits', 'FRUIT-003', 0.250, '7-9cm', true),
('Fraise Gariguette', 'Fraises Gariguette sucrées et parfumées', 4.50, 30, 'Fruits', 'FRUIT-004', 0.020, '2-3cm', true),
('Kiwi Hayward', 'Kiwis Hayward riches en vitamine C', 3.80, 45, 'Fruits', 'FRUIT-005', 0.100, '5-7cm', true);

-- Produits de la catégorie "Légumes"
INSERT INTO products (name, description, price, stock, category, sku, weight, dimensions, is_active) VALUES
('Carotte Bio', 'Carottes biologiques fraîches, parfaites pour les soupes', 1.80, 120, 'Légumes', 'LEG-001', 0.100, '15-20cm', true),
('Tomate Cerise', 'Tomates cerises sucrées, idéales pour les salades', 4.50, 80, 'Légumes', 'LEG-002', 0.020, '2-3cm', true),
('Salade Laitue', 'Salade laitue fraîche et croquante', 1.20, 90, 'Légumes', 'LEG-003', 0.300, '20-25cm', true),
('Courgette', 'Courgettes fraîches, parfaites pour les ratatouilles', 2.20, 70, 'Légumes', 'LEG-004', 0.200, '15-20cm', true),
('Brocoli Bio', 'Brocolis biologiques riches en vitamines', 3.50, 50, 'Légumes', 'LEG-005', 0.400, '12-15cm', true);

-- Produits de la catégorie "Épicerie"
INSERT INTO products (name, description, price, stock, category, sku, weight, dimensions, is_active) VALUES
('Riz Basmati', 'Riz Basmati parfumé 1kg, idéal pour les plats asiatiques', 3.50, 50, 'Épicerie', 'EPI-001', 1.000, '20x15x5cm', true),
('Pâtes Spaghetti', 'Pâtes spaghetti de blé dur 500g, qualité premium', 1.90, 70, 'Épicerie', 'EPI-002', 0.500, '25x8x3cm', true),
('Huile d''Olive Extra Vierge', 'Huile d''olive extra vierge 500ml, première pression à froid', 8.90, 30, 'Épicerie', 'EPI-003', 0.500, '15x8x8cm', true),
('Farine T65', 'Farine de blé T65 1kg, parfaite pour le pain', 2.20, 60, 'Épicerie', 'EPI-004', 1.000, '20x12x8cm', true),
('Sucre Roux', 'Sucre roux de canne 1kg, non raffiné', 3.80, 40, 'Épicerie', 'EPI-005', 1.000, '20x12x8cm', true);

-- Produits de la catégorie "Boulangerie"
INSERT INTO products (name, description, price, stock, category, sku, weight, dimensions, is_active) VALUES
('Pain de Campagne', 'Pain de campagne artisanal, cuit au feu de bois', 2.20, 25, 'Boulangerie', 'BOUL-001', 0.800, '30x12x8cm', true),
('Croissant au Beurre', 'Croissants au beurre frais, croustillants et dorés', 1.50, 40, 'Boulangerie', 'BOUL-002', 0.080, '12x6x3cm', true),
('Baguette Tradition', 'Baguette tradition française, croûte dorée', 1.20, 35, 'Boulangerie', 'BOUL-003', 0.250, '60x6x4cm', true),
('Pain Complet', 'Pain complet aux graines, riche en fibres', 2.80, 20, 'Boulangerie', 'BOUL-004', 0.600, '25x12x8cm', true),
('Chausson aux Pommes', 'Chaussons aux pommes, pâte feuilletée', 2.50, 30, 'Boulangerie', 'BOUL-005', 0.120, '15x10x3cm', true);

-- Produits de la catégorie "Produits Laitiers"
INSERT INTO products (name, description, price, stock, category, sku, weight, dimensions, is_active) VALUES
('Lait Entier Bio', 'Lait entier biologique 1L, frais du jour', 1.50, 80, 'Produits Laitiers', 'LAIT-001', 1.000, '10x8x20cm', true),
('Yaourt Nature', 'Yaourts nature au lait entier, 4x125g', 2.80, 60, 'Produits Laitiers', 'LAIT-002', 0.500, '15x10x8cm', true),
('Fromage Comté', 'Comté AOP 24 mois, 200g', 6.50, 25, 'Produits Laitiers', 'LAIT-003', 0.200, '12x8x3cm', true),
('Beurre Doux', 'Beurre doux 250g, baratte traditionnelle', 3.20, 40, 'Produits Laitiers', 'LAIT-004', 0.250, '12x8x3cm', true),
('Crème Fraîche', 'Crème fraîche épaisse 200ml', 2.20, 35, 'Produits Laitiers', 'LAIT-005', 0.200, '8x8x8cm', true);

-- Produits de la catégorie "Viandes et Poissons"
INSERT INTO products (name, description, price, stock, category, sku, weight, dimensions, is_active) VALUES
('Poulet Fermier', 'Poulet fermier élevé en plein air, 1.5kg', 12.50, 15, 'Viandes et Poissons', 'VIANDE-001', 1.500, '25x20x10cm', true),
('Saumon Frais', 'Pavé de saumon frais, 300g', 18.90, 20, 'Viandes et Poissons', 'VIANDE-002', 0.300, '15x8x3cm', true),
('Bœuf Haché', 'Bœuf haché 5% MG, 500g', 8.50, 25, 'Viandes et Poissons', 'VIANDE-003', 0.500, '15x10x3cm', true),
('Cabillaud', 'Filet de cabillaud frais, 400g', 15.20, 18, 'Viandes et Poissons', 'VIANDE-004', 0.400, '20x8x3cm', true),
('Jambon de Bayonne', 'Jambon de Bayonne IGP, 200g', 9.80, 30, 'Viandes et Poissons', 'VIANDE-005', 0.200, '20x8x2cm', true);

-- Mise à jour des timestamps
UPDATE products SET created_at = CURRENT_TIMESTAMP - INTERVAL '1 day' * (id % 7);
UPDATE products SET updated_at = CURRENT_TIMESTAMP - INTERVAL '1 hour' * (id % 24);
