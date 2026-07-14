INSERT INTO roles (id, name, created_at, created_by)
VALUES 
    (gen_random_uuid(), 'ADMIN', CURRENT_TIMESTAMP, 'system'),
    (gen_random_uuid(), 'EDITOR', CURRENT_TIMESTAMP, 'system'),
    (gen_random_uuid(), 'AUTHOR', CURRENT_TIMESTAMP, 'system')
ON CONFLICT (name) DO NOTHING;
