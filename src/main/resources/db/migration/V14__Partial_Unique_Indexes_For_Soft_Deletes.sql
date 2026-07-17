-- Remove as constraints UNIQUE antigas (que aplicam-se a registros deletados)
ALTER TABLE posts DROP CONSTRAINT IF EXISTS posts_slug_key;
ALTER TABLE categories DROP CONSTRAINT IF EXISTS categories_name_key;
ALTER TABLE categories DROP CONSTRAINT IF EXISTS categories_slug_key;

-- Cria índices parciais que garantem unicidade apenas para registros ativos
CREATE UNIQUE INDEX posts_slug_idx ON posts (slug) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX categories_name_idx ON categories (name) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX categories_slug_idx ON categories (slug) WHERE deleted_at IS NULL;
