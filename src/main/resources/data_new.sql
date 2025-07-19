-- =====================================================================
-- data.sql
-- Initial Data Load: Users & Accounts for CentralBank-SecLand
-- =====================================================================
-- Purpose:
--   - Seeds the initial set of users and bank accounts for the application.
--   - Designed to be idempotent: re-running this script will not duplicate data.
-- ---------------------------------------------------------------------
-- Notes:
--   - Passwords are hashed with BCrypt for security.
--   - Uses ON CONFLICT (PostgreSQL UPSERT) to avoid insertion errors.
--   - Admin credentials: admin/SecureAdmin123!
--   - Demo credentials: Use the registration endpoint to create test users
-- =====================================================================

-- Insert Admin User with proper BCrypt hash
-- Password: SecureAdmin123! -> $2a$10$5fLwLDy8kSf9ZBuyEf5gP.7UOb5iqy7.VBOZwV9JWcACTgkf7D91q
INSERT INTO users (id, username, password, full_name, created_at) VALUES
    (1, 'admin', '$2a$10$5fLwLDy8kSf9ZBuyEf5gP.7UOb5iqy7.VBOZwV9JWcACTgkf7D91q', 'System Administrator', NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert Bank Accounts for Admin
INSERT INTO accounts (id, account_number, account_type, balance, user_id, created_at) VALUES
    (1, 'ADMIN-001', 'Checking', 1000000.00, 1, NOW()),
    (2, 'ADMIN-002', 'Savings',  5000000.00, 1, NOW())
ON CONFLICT (id) DO NOTHING;

-- Reset sequence counters to prevent ID collisions with new inserts
SELECT setval('users_id_seq',    (SELECT GREATEST(MAX(id),    100) FROM users));
SELECT setval('accounts_id_seq', (SELECT GREATEST(MAX(id),   1000) FROM accounts));
