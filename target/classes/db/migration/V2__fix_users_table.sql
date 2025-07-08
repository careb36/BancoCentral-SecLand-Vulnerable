-- =====================================================================
-- V2__fix_users_table.sql
-- Asegura la estructura correcta de la tabla 'users' para compatibilidad con BCrypt y la aplicación
-- =====================================================================

-- Modifica el campo 'password' para asegurar longitud suficiente para BCrypt
ALTER TABLE users ALTER COLUMN password TYPE VARCHAR(100);
ALTER TABLE users ALTER COLUMN password SET NOT NULL;

-- Modifica el campo 'username' para asegurar unicidad y longitud
ALTER TABLE users ALTER COLUMN username TYPE VARCHAR(50);
ALTER TABLE users ALTER COLUMN username SET NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS users_username_idx ON users(username);

-- Modifica el campo 'full_name' para asegurar longitud y obligatoriedad
ALTER TABLE users ALTER COLUMN full_name TYPE VARCHAR(100);
ALTER TABLE users ALTER COLUMN full_name SET NOT NULL;

-- Asegura que 'created_at' es TIMESTAMP y obligatorio
ALTER TABLE users ALTER COLUMN created_at TYPE TIMESTAMP;
ALTER TABLE users ALTER COLUMN created_at SET NOT NULL; 