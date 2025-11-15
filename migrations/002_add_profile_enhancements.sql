-- Migration: Add Profile enhancements for civic engagement
-- Date: 2024
-- Description: Adds WhatsApp number, social media links, and civic interests to profiles table

-- ============================================
-- PROFILE TABLE ENHANCEMENTS
-- ============================================

ALTER TABLE profiles
ADD COLUMN whatsapp_number VARCHAR(20) NULL COMMENT 'WhatsApp contact number',
ADD COLUMN social_media_links TEXT NULL COMMENT 'JSON object with social media URLs (Facebook, Twitter, etc.)',
ADD COLUMN civic_interests TEXT NULL COMMENT 'JSON array of civic interests/topics (infrastructure, education, health, etc.)';

