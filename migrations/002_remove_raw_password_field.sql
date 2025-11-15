-- Migration: Remove Raw Password Field
-- Date: 2025-01-XX
-- Description: Drops the raw_password column from the users table.
--              This field was temporarily used for testing purposes when
--              email delivery was not available. Now that email and SMS
--              functionality is working, this field is no longer needed.
--
-- IMPORTANT: Run this script AFTER deploying the updated code that removes
--            the rawPassword field from the User entity.

ALTER TABLE users DROP COLUMN IF EXISTS raw_password;

-- Verify the column removal
-- SELECT column_name 
-- FROM information_schema.columns 
-- WHERE table_name = 'users' AND column_name = 'raw_password';
-- Should return no rows

