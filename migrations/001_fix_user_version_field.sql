-- Migration: Fix User Version Field Initialization
-- Date: 2025-11-15
-- Description: Updates all existing User records with NULL version values to 0
--              This fixes the NullPointerException that occurs when Hibernate
--              tries to increment a null version field during entity updates.
--
-- IMPORTANT: Run this script BEFORE deploying the updated code that initializes
--            the version field to 0L in the User entity.

UPDATE users SET version = 0 WHERE version IS NULL;

-- Verify the update
-- SELECT COUNT(*) FROM users WHERE version IS NULL; -- Should return 0

