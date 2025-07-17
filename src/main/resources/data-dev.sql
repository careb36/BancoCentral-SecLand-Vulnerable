-- =====================================================================
-- data-dev.sql
-- Initial Data Load for H2 Development Database
-- =====================================================================

-- Insert Users (H2 compatible - using MERGE instead of ON CONFLICT)
MERGE INTO users (id, username, password, full_name, created_at) VALUES
(1, 'testuser', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Usuario de Prueba', CURRENT_TIMESTAMP),
(2, 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Administrador', CURRENT_TIMESTAMP),
(3, 'carolina.fernandez', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Carolina Fernández', CURRENT_TIMESTAMP),
(4, 'rebeca.pereira', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Rebeca Pereira', CURRENT_TIMESTAMP);

-- Insert Bank Accounts (H2 compatible)
MERGE INTO bank_accounts (id, account_number, account_type, balance, user_id, created_at) VALUES
(1, '1000000001', 'SAVINGS', 1500.00, 1, CURRENT_TIMESTAMP),
(2, '1000000002', 'CHECKING', 2500.00, 1, CURRENT_TIMESTAMP),
(3, '1000000003', 'SAVINGS', 5000.00, 2, CURRENT_TIMESTAMP),
(4, '1000000004', 'CHECKING', 3000.00, 3, CURRENT_TIMESTAMP),
(5, '1000000005', 'SAVINGS', 1200.00, 3, CURRENT_TIMESTAMP),
(6, '1000000006', 'CHECKING', 800.00, 4, CURRENT_TIMESTAMP);

-- Insert some sample transactions
MERGE INTO transactions (id, transaction_type, amount, from_account_id, to_account_id, description, created_at) VALUES
(1, 'DEPOSIT', 1500.00, NULL, 1, 'Depósito inicial', CURRENT_TIMESTAMP),
(2, 'DEPOSIT', 2500.00, NULL, 2, 'Depósito inicial', CURRENT_TIMESTAMP),
(3, 'DEPOSIT', 5000.00, NULL, 3, 'Depósito inicial', CURRENT_TIMESTAMP),
(4, 'TRANSFER', 200.00, 1, 2, 'Transferencia de prueba', CURRENT_TIMESTAMP);
