INSERT INTO Сategory (category_number, name) VALUES
                                                          (1, 'Dairy'),
                                                          (2, 'Bakery'),
                                                          (3, 'Drinks');

INSERT INTO Employee
(id_employee, surname, name, patronymic, role, salary, date_of_birth, date_of_start, phone_number, city, street, zip_code)
VALUES
    ('E001', 'Ivanenko', 'Olena', 'Petrovna', 'MANAGER', 25000.00, '1995-05-12', '2023-01-10', '+380971112233', 'Kyiv', 'Shevchenka 1', '01001'),

    ('E002', 'Koval', 'Ihor', 'Stepanovych', 'CASHIER', 18000.00, '2000-03-18', '2024-02-01', '+380931234567', 'Kyiv', 'Khreshchatyk 2', '01001');

INSERT INTO Customer_Card
(card_number, surname, name, patronymic, phone_number, city, street, zip_code, percent)
VALUES
    ('C001', 'Melnyk', 'Anna', 'Ivanivna', '+380991234567', 'Kyiv', 'Lvivska 10', '02000', 5);

INSERT INTO Product
(id_product, name, manufacturer, characteristics, category_number)
VALUES
    (1, 'Milk 2.5%', 'Yagotynske', '1L package', 1),
    (2, 'Bread White', 'Kyiv Bakery', '500g', 2),
    (3, 'Orange Juice', 'Sandora', '1L', 3);

INSERT INTO Store_Product
(UPC, UPC_prom, id_product, selling_price, products_number, promotional_product)
VALUES
    ('UPC001', NULL, 1, 52.00, 40, FALSE),
    ('UPC002', NULL, 2, 28.00, 60, FALSE),
    ('UPC003', NULL, 3, 75.00, 25, TRUE);