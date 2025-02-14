-- Видалення таблиць у правильному порядку, щоб уникнути помилок через залежності
DROP TABLE IF EXISTS customer_employer;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS employers;

-- Створення таблиці customers
CREATE TABLE customers (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           surname VARCHAR(100) NOT NULL,
                           email VARCHAR(150) UNIQUE NOT NULL,
                           age INT CHECK (age >= 18)
);

-- Створення таблиці accounts
CREATE TABLE accounts (
                          id SERIAL PRIMARY KEY,
                          account_number VARCHAR(50) UNIQUE NOT NULL,
                          currency VARCHAR(3) NOT NULL CHECK (currency IN ('USD', 'EUR', 'UAH', 'CHF', 'GBP')),
                          balance DOUBLE PRECISION CHECK (balance >= 0),
                          customer_id INT,
                          FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- Створення таблиці employers
CREATE TABLE employers (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL UNIQUE,
                           address VARCHAR(255) NOT NULL
);

-- Створення проміжної таблиці для зв’язку "багато-до-багатьох"
CREATE TABLE customer_employer (
                                   customer_id INT NOT NULL,
                                   employer_id INT NOT NULL,
                                   PRIMARY KEY (customer_id, employer_id),
                                   FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
                                   FOREIGN KEY (employer_id) REFERENCES employers(id) ON DELETE CASCADE
);
