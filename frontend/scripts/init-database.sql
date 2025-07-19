-- Initialize SecLand Central Bank Database
-- This script creates the necessary tables for the banking application

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create accounts table
CREATE TABLE IF NOT EXISTS accounts (
    id SERIAL PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('Savings', 'Checking')),
    balance DECIMAL(15,2) DEFAULT 0.00,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    transaction_type VARCHAR(20) NOT NULL CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'RECEIVED')),
    amount DECIMAL(15,2) NOT NULL,
    description TEXT,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    source_account_id INTEGER REFERENCES accounts(id),
    destination_account_id INTEGER REFERENCES accounts(id),
    source_account_number VARCHAR(20),
    destination_account_number VARCHAR(20)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_accounts_user_id ON accounts(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_source_account ON transactions(source_account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_destination_account ON transactions(destination_account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(transaction_date);

-- Insert demo user
INSERT INTO users (username, password_hash, full_name) 
VALUES ('testuser', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Test User')
ON CONFLICT (username) DO NOTHING;

-- Insert demo accounts for test user
INSERT INTO accounts (account_number, account_type, balance, user_id)
SELECT 'TEST-SAVINGS-001', 'Savings', 1000.00, u.id
FROM users u WHERE u.username = 'testuser'
ON CONFLICT (account_number) DO NOTHING;

INSERT INTO accounts (account_number, account_type, balance, user_id)
SELECT 'TEST-CHECKING-001', 'Checking', 500.00, u.id
FROM users u WHERE u.username = 'testuser'
ON CONFLICT (account_number) DO NOTHING;

-- Insert demo transactions
INSERT INTO transactions (transaction_type, amount, description, source_account_number, destination_account_number, source_account_id, destination_account_id)
SELECT 'DEPOSIT', 1000.00, 'Initial deposit', NULL, 'TEST-SAVINGS-001', NULL, a.id
FROM accounts a WHERE a.account_number = 'TEST-SAVINGS-001'
ON CONFLICT DO NOTHING;

INSERT INTO transactions (transaction_type, amount, description, source_account_number, destination_account_number, source_account_id, destination_account_id)
SELECT 'DEPOSIT', 500.00, 'Initial deposit', NULL, 'TEST-CHECKING-001', NULL, a.id
FROM accounts a WHERE a.account_number = 'TEST-CHECKING-001'
ON CONFLICT DO NOTHING;
