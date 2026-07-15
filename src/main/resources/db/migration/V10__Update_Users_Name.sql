-- Alteração do campo name para first_name
ALTER TABLE users RENAME COLUMN name TO first_name;

-- Adição da coluna last_name, inicialmente nullable para não quebrar cadastros existentes, mas usaremos na entidade para salvar.
ALTER TABLE users ADD COLUMN last_name VARCHAR(255);
