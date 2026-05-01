-- Migration: Ensure 'role' column has correct values
-- Safe to run multiple times on every restart

-- Patch any existing rows with NULL or empty role to 'STUDENT'
UPDATE `users` SET `role` = 'STUDENT' WHERE `role` IS NULL OR `role` = '' OR `role` = 'null';

-- Ensure the admin account exists with ADMIN role via INSERT IGNORE
INSERT IGNORE INTO `users` (`name`, `email`, `password`, `role`)
VALUES ('Super Admin', 'admin@quizmaster.com', 'admin123', 'ADMIN');

-- Ensure the admin has the correct role even if the row existed with wrong/null role
UPDATE `users` SET `role` = 'ADMIN' WHERE `email` = 'admin@quizmaster.com';
