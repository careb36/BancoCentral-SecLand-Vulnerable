-- =====================================================================
-- V3__allow_null_source_account_id.sql
-- Flyway Migration: Allow NULL values in source_account_id column
-- =====================================================================
-- Purpose:
--   - Modify the transactions table to allow NULL values in source_account_id
--   - This enables external deposits (initial deposits) where there's no source account
-- =====================================================================

-- Allow NULL values in source_account_id column for external deposits
-- source_account_id: Account ID from which funds are withdrawn. NULL for external deposits (initial deposits, bank deposits, etc.)
ALTER TABLE transactions 
ALTER COLUMN source_account_id BIGINT NULL;
