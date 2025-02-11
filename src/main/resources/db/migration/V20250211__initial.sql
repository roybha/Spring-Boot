-- Створення таблиці customers, якщо вона ще не існує
CREATE TABLE IF NOT EXISTS customers (
                                         id SERIAL PRIMARY KEY,           -- первинний ключ, автоматично інкрементується
                                         name VARCHAR(100) NOT NULL,      -- ім'я, обов'язкове поле
                                         surname VARCHAR(100) NOT NULL,   -- прізвище, обов'язкове поле
                                         email VARCHAR(150) UNIQUE NOT NULL, -- електронна пошта, обов'язкове, унікальне
                                         age INT CHECK (age >= 18)        -- вік, має бути не менше 18
);

-- Створення типу ENUM для валют, якщо він ще не існує
-- Створення типу ENUM для валют
DO $$
    BEGIN
        -- Перевіряємо чи існує тип, і якщо ні, створюємо його
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'currency_type') THEN
            CREATE TYPE currency_type AS ENUM ('USD', 'EUR', 'UAH', 'CHF', 'GBP');
        END IF;
    END;
$$;


-- Створення таблиці accounts, якщо вона ще не існує
CREATE TABLE IF NOT EXISTS accounts (
                                        id SERIAL PRIMARY KEY,              -- первинний ключ, автоматично інкрементується
                                        account_number VARCHAR(50) UNIQUE NOT NULL,  -- номер рахунку, унікальний
                                        currency currency_type NOT NULL,     -- валюта, обмежена значеннями з ENUM
                                        balance DOUBLE PRECISION CHECK (balance >= 0), -- баланс, не може бути від'ємним
                                        customer_id INT,                    -- зовнішній ключ для зв'язку з таблицею customers
                                        FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE -- зовнішній ключ на таблицю customers
);

