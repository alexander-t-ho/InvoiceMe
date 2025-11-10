-- Migration script to set default password "123456" for all customers without a password
-- This script should be run after deploying the password feature
-- The password hash is for "123456" using BCrypt

-- Note: This is a one-time migration. In production, you would want to:
-- 1. Generate unique BCrypt hashes for each customer
-- 2. Send password reset emails to customers
-- 3. Or use a more secure default password generation

-- For now, we'll set a default BCrypt hash for "123456"
-- This hash was generated using: BCryptPasswordEncoder.encode("123456")
-- You can generate a new one if needed, but this one will work for all customers

UPDATE customers 
SET password_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
WHERE password_hash IS NULL OR password_hash = '';

-- After running this migration, you can set the column to NOT NULL:
-- ALTER TABLE customers ALTER COLUMN password_hash SET NOT NULL;

