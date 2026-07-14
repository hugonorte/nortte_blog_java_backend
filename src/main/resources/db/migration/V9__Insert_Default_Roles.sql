INSERT INTO roles (id, name, created_at, created_by)
SELECT '3c28ec22-c350-48e0-ac38-a1e4c703e33b', 'ADMIN', CURRENT_TIMESTAMP, 'system'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');

INSERT INTO roles (id, name, created_at, created_by)
SELECT '53b8f106-bc3e-4b7f-b5ec-ffbf8193f211', 'EDITOR', CURRENT_TIMESTAMP, 'system'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'EDITOR');

INSERT INTO roles (id, name, created_at, created_by)
SELECT '70327f27-6b3a-4137-b6a6-f2cd38a2ad34', 'AUTHOR', CURRENT_TIMESTAMP, 'system'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'AUTHOR');
