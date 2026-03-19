-- Fix admin password hash to match Admin@123
UPDATE users
SET password_hash = '$2b$10$/QHj8RynZYS.ucQjPR28.uNM54UioCWe2jn2BchsoEklwwUDtc4Iu'
WHERE email = 'admin@goldloan.com';
