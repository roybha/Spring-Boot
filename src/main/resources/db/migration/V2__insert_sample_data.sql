

INSERT INTO employers (name, address) VALUES
                                          ('Група Приват', 'Дніпро'),
                                          ('Інтерконтіненталь & Co', 'Дніпро');

INSERT INTO customers (name, surname, email, age) VALUES
                                                      ('Дмитро', 'Данильченко', 'dmytro.dan@example.com', 25),
                                                      ('Олексій', 'Сергієнко', 'alex.serhienko@gmail.com', 30);


INSERT INTO customer_employer (customer_id, employer_id) VALUES
                                                             (1, 1),
                                                             (2, 2);
