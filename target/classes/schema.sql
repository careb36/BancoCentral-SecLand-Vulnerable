-- =====================================================================
-- schema.sql
-- Database Schema for CentralBank-SecLand Banking Application
-- =====================================================================
-- Purpose:
--   - Creates the database tables for users, accounts, and transactions
--   - Defines proper constraints, indexes, and relationships
--   - Ensures data integrity and referential constraints
-- =====================================================================

-- =====================================================================
-- USERS TABLE
-- =====================================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index for username lookups (authentication)
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

-- =====================================================================
-- ACCOUNTS TABLE
-- =====================================================================
CREATE TABLE IF NOT EXISTS accounts (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(36) NOT NULL UNIQUE,
    account_type VARCHAR(20) NOT NULL,
    balance DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Check constraints
    CONSTRAINT chk_account_type CHECK (account_type IN ('Savings', 'Checking')),
    CONSTRAINT chk_balance_non_negative CHECK (balance >= 0)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_accounts_user_id ON accounts(user_id);
CREATE INDEX IF NOT EXISTS idx_accounts_number ON accounts(account_number);

-- =====================================================================
-- TRANSACTIONS TABLE
-- =====================================================================
CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    source_account_id BIGINT, -- allow NULL for external deposits
    destination_account_id BIGINT NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    description VARCHAR(255),
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraints
    CONSTRAINT fk_transactions_source_account FOREIGN KEY (source_account_id) REFERENCES accounts(id) ON DELETE RESTRICT,
    CONSTRAINT fk_transactions_dest_account FOREIGN KEY (destination_account_id) REFERENCES accounts(id) ON DELETE RESTRICT,
    
    -- Check constraints
    CONSTRAINT chk_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_different_accounts CHECK (source_account_id != destination_account_id)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_transactions_source_account ON transactions(source_account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_dest_account ON transactions(destination_account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(transaction_date);

-- =====================================================================
-- SEQUENCES (for ID generation)
-- =====================================================================
-- These sequences are automatically created by BIGSERIAL, but we can customize them if needed
-- The sequences will be: users_id_seq, accounts_id_seq, transactions_id_seq 