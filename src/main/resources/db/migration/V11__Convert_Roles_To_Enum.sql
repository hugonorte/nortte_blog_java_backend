-- Adiciona a coluna 'role' na tabela users
ALTER TABLE users ADD COLUMN role VARCHAR(50);

-- Migra a role do usuário (pega a primeira encontrada na tabela de ligação)
UPDATE users
SET role = (
    SELECT r.name 
    FROM roles r 
    JOIN user_roles ur ON ur.role_id = r.id 
    WHERE ur.user_id = users.id 
    LIMIT 1
);

-- Para usuários que porventura não tinham role ou deu nulo, seta um valor padrão seguro
UPDATE users SET role = 'USER' WHERE role IS NULL;

-- Garante que o campo não seja nulo daqui pra frente (opcional, dependendo do design, mas recomendado)
ALTER TABLE users ALTER COLUMN role SET NOT NULL;

-- Dropa as tabelas antigas (atenção: dados serão deletados, mas já foram migrados acima)
DROP TABLE user_roles;
DROP TABLE roles;
