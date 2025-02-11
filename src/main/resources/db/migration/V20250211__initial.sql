-- Створення таблиці customers з певними обмеженнями на всі рядки
CREATE TABLE customers (
                           id SERIAL PRIMARY KEY,           -- первинний ключ, автоматично інкрементується
                           name VARCHAR(100) NOT NULL,      -- ім'я, обов'язкове поле
                           surname VARCHAR(100) NOT NULL,   -- прізвище, обов'язкове поле
                           email VARCHAR(150) UNIQUE NOT NULL, -- електронна пошта, обов'язкове, унікальне
                           age INT CHECK (age >= 18)        -- вік, має бути не менше 18
);

-- Створення типу ENUM для валют
CREATE TYPE currency_type AS ENUM ('USD', 'EUR', 'UAH', 'CHF', 'GBP');

-- Створення таблиці accounts з обмеженням на валюту
CREATE TABLE accounts (
                          id SERIAL PRIMARY KEY,              -- первинний ключ, автоматично інкрементується
                          account_number VARCHAR(50) UNIQUE NOT NULL,  -- номер рахунку, унікальний
                          currency currency_type NOT NULL,     -- валюта, обмежена значеннями з ENUM
                          balance DOUBLE PRECISION CHECK (balance >= 0), -- баланс, не може бути від'ємним
                          customer_id INT,                    -- зовнішній ключ для зв'язку з таблицею customers
                          FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE -- зовнішній ключ на таблицю customers
);