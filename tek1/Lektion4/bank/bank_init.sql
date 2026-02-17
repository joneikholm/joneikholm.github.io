CREATE DATABASE IF NOT EXISTS bankdb;
USE bankdb;

DROP TABLE IF EXISTS account;

CREATE TABLE account (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(50),
                         saldo INT
);

INSERT INTO account (name, saldo) VALUES
                                      ('Alice', 1000),
                                      ('Bob', 1000),
                                      ('Barnet', 1000);