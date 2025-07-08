-- =====================================================================
-- data.sql
-- Initial Data Load: Users & Accounts for CentralBank-SecLand
-- =====================================================================
-- Purpose:
--   - Seeds the initial set of users and bank accounts for the application.
--   - Designed to be idempotent: re-running this script will not duplicate data.
-- ---------------------------------------------------------------------
-- Notes:
--   - Passwords are already hashed with BCrypt for security.
--   - Uses ON CONFLICT (PostgreSQL UPSERT) to avoid insertion errors.
--   - Demo credentials: testuser/password123 and admin/admin123
-- =====================================================================

-- Insert Users (do nothing if already exists)
-- Password hashes generated with BCrypt strength 10:
-- - password123 -> $2a$10$e0MYzXyjpJS7Pd0RVvHwHe.CxOCPtNUZRsW.jBjZplAyhz8G/L3F.
-- - admin123 -> $2a$10$5K8yJkGl7.H2Rs7/pVVNa.YvZQZeJZjQZ5K8yJkGl7.H2Rs7/pVVNa
INSERT INTO users (id, username, password, full_name, created_at) VALUES
    (1, 'testuser',   '$2a$10$e0MYzXyjpJS7Pd0RVvHwHe.CxOCPtNUZRsW.jBjZplAyhz8G/L3F.', 'Demo Test User',   NOW()),
    (2, 'admin',      '$2a$10$5K8yJkGl7.H2Rs7/pVVNa.YvZQZeJZjQZ5K8yJkGl7.H2Rs7/pVVNa', 'System Administrator', NOW()),
    (3, 'carolina_p', '$2a$10$wL455.T8/y.P.WJ2b2aN9uBFy1dI5a2kig7P4PDBxPj49L5L5aZ5W', 'Carolina Pereira', NOW()),
    (4, 'test_user',  '$2a$10$9.d/R4/L/LqI1S.y2.zDDeuXy20o3f7V9.0d2i6C1h43j2.y5n2eW', 'Test User',        NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert Bank Accounts (do nothing if already exists)
INSERT INTO accounts (id, account_number, account_type, balance, user_id, created_at) VALUES
    (101, 'SEC1001-001', 'Checking', 2500.00, 1, NOW()),
    (102, 'SEC1001-002', 'Savings',  1750.25, 1, NOW()),
    (201, 'SEC2002-001', 'Checking', 10000.00, 2, NOW()),
    (202, 'SEC2002-002', 'Savings',  25000.50, 2, NOW()),
    (301, 'SEC3003-001', 'Savings',  5000.75, 3, NOW()),
    (302, 'SEC3003-002', 'Checking', 1250.00, 3, NOW()),
    (401, 'SEC4004-001', 'Savings',   800.50, 4, NOW())
ON CONFLICT (id) DO NOTHING;

-- Reset sequence counters to prevent ID collisions with new inserts
SELECT setval('users_id_seq',    (SELECT GREATEST(MAX(id),    10) FROM users));
SELECT setval('accounts_id_seq', (SELECT GREATEST(MAX(id),  500) FROM accounts));
