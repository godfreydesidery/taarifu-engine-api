-- Migration: Add phone number to users table
-- Date: 2024
-- Description: Adds phone_number column to users table for admin user phone number support

-- ============================================
-- USERS TABLE ENHANCEMENTS
-- ============================================

ALTER TABLE users
ADD COLUMN phone_number VARCHAR(20) NULL COMMENT 'Phone number for admin users (optional)';

CREATE INDEX idx_phone_number ON users(phone_number);

