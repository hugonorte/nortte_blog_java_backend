-- Remover constraint de chave estrangeira
ALTER TABLE authors DROP CONSTRAINT IF EXISTS fk_author_user;
-- Caso exista pelo nome da constraint do H2 (nas migrations anteriores)
ALTER TABLE authors DROP CONSTRAINT IF EXISTS uk_authors_user_id;

-- Adicionar novos campos
ALTER TABLE authors ADD COLUMN name VARCHAR(255);
ALTER TABLE authors ADD COLUMN email VARCHAR(255);

-- Para garantir consistência em dados existentes, populamos com valores temporários antes de setar NOT NULL
UPDATE authors SET name = 'Unknown Author', email = 'unknown@example.com' WHERE name IS NULL;

-- Tornar campos NOT NULL
ALTER TABLE authors ALTER COLUMN name SET NOT NULL;
ALTER TABLE authors ALTER COLUMN email SET NOT NULL;

-- Remover campo user_id
ALTER TABLE authors DROP COLUMN user_id;
