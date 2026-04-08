INSERT INTO category (category_number, category_name) VALUES
                                                          (1, 'Dairy'),
                                                          (2, 'Bakery'),
                                                          (3, 'Drinks');

INSERT INTO employee
(id_employee, surname, name, patronymic, position, salary, start_date, birth_date, phone, city, street, zip_code)
VALUES
    ('E001', 'Ivanenko', 'Olena', 'Petrovna', 'MANAGER', 25000.00, '2023-01-10', '1995-05-12', '+380971112233', 'Kyiv', 'Shevchenka 1', '01001'),
    ('E002', 'Koval', 'Ihor', 'Stepanovych', 'CASHIER', 18000.00, '2024-02-01', '2000-03-18', '+380931234567', 'Kyiv', 'Khreshchatyk 2', '01001');

INSERT INTO customer_card
(card_number, surname, name, patronymic, phone, city, street, zip_code, discount_percent)
VALUES
    ('C001', 'Melnyk', 'Anna', 'Ivanivna', '+380991234567', 'Kyiv', 'Lvivska 10', '02000', 5.00);

INSERT INTO product
(id_product, product_name, manufacturer, characteristics, category_number)
VALUES
    ('P001', 'Milk 2.5%', 'Yagotynske', '1L package', 1),
    ('P002', 'Bread White', 'Kyiv Bakery', '500g', 2),
    ('P003', 'Orange Juice', 'Sandora', '1L', 3);

INSERT INTO store_product
(upc, id_product, selling_price, product_count, is_promotional)
VALUES
    ('UPC001', 'P001', 52.00, 40, FALSE),
    ('UPC002', 'P002', 28.00, 60, FALSE),
    ('UPC003', 'P003', 75.00, 25, TRUE);