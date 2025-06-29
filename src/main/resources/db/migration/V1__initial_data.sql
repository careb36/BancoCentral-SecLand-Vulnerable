-- =====================================================================
-- V1__initial_data.sql
-- Initial Data Load: Users & Accounts
-- ---------------------------------------------------------------------
-- Purpose:
--   - Seeds the initial set of users and bank accounts for the BancoCentral-SecLand application.
--   - Designed to be idempotent: re-running this script will not duplicate data.
-- ---------------------------------------------------------------------
-- Notes:
--   - Passwords are already hashed with BCrypt for security.
--   - Uses ON CONFLICT (PostgreSQL UPSERT) to avoid insertion errors.
--   - Sequence reset ensures future IDs do not conflict with seeded data.
-- =====================================================================

-- Insert Users (do nothing if already exists)
INSERT INTO users (id, username, password, full_name, created_at) VALUES
                                                                      (1, 'carolina_p', '$2a$10$wL455.T8/y.P.WJ2b2aN9uBFy1dI5a2kig7P4PDBxPj49L5L5aZ5W', 'Carolina Pereira', NOW()),
                                                                      (2, 'test_user',  '$2a$10$9.d/R4/L/LqI1S.y2.zDDeuXy20o3f7V9.0d2i6C1h43j2.y5n2eW', 'Test User',        NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert Bank Accounts (do nothing if already exists)
INSERT INTO accounts (id, account_number, account_type, balance, user_id, created_at) VALUES
                                                                                          (101, 'SEC1001-001', 'Savings',  5000.75, 1, NOW()),
                                                                                          (102, 'SEC1001-002', 'Checking', 1250.00, 1, NOW()),
                                                                                          (201, 'SEC2002-001', 'Savings',   800.50, 2, NOW())
ON CONFLICT (id) DO NOTHING;

-- Reset sequence counters to prevent ID collisions with new inserts
SELECT setval('users_id_seq',    (SELECT GREATEST(MAX(id),    1) FROM users));
SELECT setval('accounts_id_seq', (SELECT GREATEST(MAX(id),  100) FROM accounts));
