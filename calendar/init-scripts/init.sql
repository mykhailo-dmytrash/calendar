
CREATE DATABASE IF NOT EXISTS calendar CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user if it doesn't exist
CREATE USER IF NOT EXISTS 'calendar_user'@'%' IDENTIFIED BY 'calendar_password';

-- Grant all privileges on the calendar database to the user
GRANT ALL PRIVILEGES ON calendar.* TO 'calendar_user'@'%';

-- Flush privileges to ensure changes take effect
FLUSH PRIVILEGES;

