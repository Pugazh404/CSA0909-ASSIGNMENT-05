CREATE DATABASE IF NOT EXISTS assignment5;
USE assignment5;
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(40) PRIMARY KEY,
    pwd VARCHAR(100) NOT NULL
);
INSERT INTO users(id,pwd) VALUES ('student1','demo-password')
ON DUPLICATE KEY UPDATE pwd=VALUES(pwd);
