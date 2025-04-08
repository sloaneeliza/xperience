-- event.sql
DROP DATABASE IF EXISTS wright_db;
CREATE DATABASE wright_db;

USE wright_db;

DROP TABLE IF EXISTS Event;
CREATE TABLE Event (
    name VARCHAR(255) PRIMARY KEY,
    date DATE NOT NULL,
    time TIME NOT NULL,
    description TEXT NOT NULL
);

INSERT INTO Event VALUES 
('Welcome Party', '2025-06-15', '19:00:00', 'First event of the season!');

DROP USER IF EXISTS 'xperience_user'@'%';
CREATE USER 'xperience_user'@'%' IDENTIFIED BY 'xperience_pass';
GRANT SELECT, INSERT, UPDATE ON wright_db.* TO 'xperience_user'@'%';
FLUSH PRIVILEGES;