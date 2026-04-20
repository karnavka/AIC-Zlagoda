INSERT INTO Category (category_number, name)
VALUES
(1, 'Dairy'),
(2, 'Bakery'),
(3, 'Drinks'),
(4, 'Vegetables'),
(5, 'Fruits'),
(6, 'Confectionery'),
(7, 'Meat');

INSERT INTO Employee
(id_employee, surname, name, patronymic, role, salary, date_of_birth, date_of_start, phone_number, city, street, zip_code)
VALUES
    ('E001', 'Ivanenko', 'Olena', 'Petrivna', 'MANAGER', 25000.00, '1995-05-12', '2023-01-10', '+380971112233', 'Kyiv', 'Shevchenka 1', '01001'),
    ('E002', 'Koval', 'Ihor', 'Stepanovych', 'CASHIER', 18000.00, '2000-03-18', '2024-02-01', '+380931234567', 'Kyiv', 'Khreshchatyk 2', '01001'),
    ('E003', 'Bondar', 'Mariya', 'Vasylivna', 'CASHIER', 17500.00, '1998-11-25', '2023-11-15', '+380509876543', 'Lviv', 'Halytska 10', '79000'),
    ('E004', 'Shevchenko', 'Andriy', 'Mykolayovych', 'MANAGER', 18500.00, '1992-07-07', '2022-05-20', '+380671112233', 'Odesa', 'Deribasivska 5', '65000'),
    ('E005', 'Tkachenko', 'Oksana', 'Ivanivna', 'CASHIER', 18000.00, '2001-01-30', '2024-01-05', '+380995554433', 'Dnipro', 'Polya 12', '49000'),
    ('E006', 'Klymenko', 'Yuliya', 'Serhiyivna', 'CASHIER', 19000.00, '1994-09-02', '2022-01-15', '+380687778899', 'Lviv', 'Franka 8', '79005');

INSERT INTO Customer_Card
(card_number, surname, name, patronymic, phone_number, city, street, zip_code, percent)
VALUES
    ('C001', 'Melnyk', 'Anna', 'Ivanivna', '+380991234567', 'Kyiv', 'Lvivska 10', '02000', 5),
    ('C002', 'Petrenko', 'Ivan', 'Vasylovych', '+380501112233', 'Kyiv', 'Polova 5', '03056', 10),
    ('C003', 'Bondarenko', 'Tetiana', 'Vitaliyivna', '+380502223344', 'Lviv', 'Shevchenka 5', '79000', 3),
    ('C004', 'Murlyk', 'Oleh', 'Mykolayovych', '+380634445566', 'Kyiv', 'Stusa 12', '65000', 7),
    ('C005', 'Goryn', 'Olena', 'Serhiyivna', '+380975556677', 'Dnipro', 'Polya 1', '49000', 2),
    ('C006', 'Lysak', 'Vasyl', 'Petrovych', '+380938889900', 'Kharkiv', 'Sumska 20', '61001', 5),
    ('C007', 'Kotyk', 'Iryna', 'Andriyivna', '+380661239876', 'Kyiv', 'Harmatna 5', '03067', 8),
    ('C008', 'Stus', 'Serhiy', 'Ihorovych', '+380684561234', 'Lviv', 'Franka 40', '79013', 1);

INSERT INTO Product
(id_product, name, manufacturer, characteristics, category_number)
VALUES
    (1, 'Milk 2.5%', 'Yagotynske', '1L package', 1),
    (2, 'Bread White', 'Kyiv Bakery', '500g', 2),
    (3, 'Orange Juice', 'Sandora', '1L', 3),
    (4, 'Potatoes', 'Local Farm', '1kg, yellow', 4),
    (5, 'Apples Gala', 'Garden Fresh', '1kg, sweet', 5),
    (6, 'Chocolate Dark', 'Roshen', '85% cocoa, 80g', 6),
    (7, 'Chicken Fillet', 'Nasha Ryaba', 'Fresh, ~600g', 7),
    (8, 'Yogurt Strawberry', 'Galychyna', '280g, bottle', 1),
    (9, 'Croissant', 'Lviv Bakery', '70g, chocolate filling', 2),
    (10, 'Coca-Cola', 'Coca-Cola HBC', '0.5L plastic bottle', 3),
    (11, 'Butter 82%', 'Ferma', '200g pack', 1),
    (12, 'Sour Cream 15%', 'President', '350g cup', 1),
    (13, 'Hard Cheese', 'Zveny Hora', '200g, sliced', 1),
    (14, 'Baguette', 'French Bakery', '250g, wheat', 2),
    (15, 'Rye Bread', 'Kyiv Bakery', '400g, sliced', 2),
    (16, 'Muffin', 'Lviv Bakery', '100g, vanilla', 2),
    (17, 'Mineral Water', 'Morshynska', '1.5L, still', 3),
    (18, 'Coffee Beans', 'Lavazza', '250g, Arabica', 3),
    (19, 'Green Tea', 'Greenfield', '25 bags per pack', 3),
    (20, 'Tomatoes', 'Greenhouse', '1kg, red', 4),
    (21, 'Cucumbers', 'Local Farm', '1kg, short', 4),
    (22, 'Onions', 'Local Farm', '1kg, yellow', 4),
    (23, 'Bananas', 'Ecuador Premium', '1kg, yellow', 5),
    (24, 'Oranges', 'Sun Fruit', '1kg, juicy', 5),
    (25, 'Lemons', 'Sun Fruit', '500g pack', 5),
    (26, 'Cookies Maria', 'Yarych', '150g pack', 6),
    (27, 'Waffles', 'Artek', '75g, classic', 6),
    (28, 'Gummy Bears', 'Haribo', '100g pack', 6),
    (29, 'Pork Steak', 'Meat Master', 'Fresh, ~500g', 7),
    (30, 'Salami Premium', 'Alan', '300g, smoked', 7),
    (31, 'Bacon', 'Yatran', '150g, sliced', 7);

INSERT INTO Store_Product
(UPC, UPC_prom, id_product, selling_price, products_number, promotional_product)
VALUES
    ('UPC011P', NULL, 11, 68.00, 10, TRUE),
    ('UPC012P', NULL, 12, 38.50, 10, TRUE),
    ('UPC013P', NULL, 13, 92.00, 8, TRUE),
    ('UPC014P', NULL, 14, 29.90, 15, TRUE),
    ('UPC010P', NULL, 10, 26.00, 30, TRUE),
    ('UPC004P', NULL, 4, 19.50, 40, TRUE),
    ('UPC001', NULL, 1, 52.00, 40, FALSE),
    ('UPC011', 'UPC011P', 11, 85.00, 20, FALSE),
    ('UPC012', 'UPC012P', 12, 45.00, 15, FALSE),
    ('UPC013', NULL, 13, 115.00, 12, FALSE),
    ('UPC002', NULL, 2, 28.00, 60, FALSE),
    ('UPC014', 'UPC014P', 14, 35.00, 25, FALSE),
    ('UPC015', NULL, 15, 24.00, 30, FALSE),
    ('UPC016', NULL, 16, 22.00, 40, TRUE),
    ('UPC003', NULL, 3, 75.00, 25, TRUE),
    ('UPC010', 'UPC010P', 10, 32.00, 50, FALSE),
    ('UPC017', NULL, 17, 18.00, 100, FALSE),
    ('UPC018', NULL, 18, 250.00, 12, FALSE),
    ('UPC019', NULL, 19, 65.00, 45, FALSE),
    ('UPC004', 'UPC004P', 4, 25.00, 100, FALSE),
    ('UPC020', NULL, 20, 95.00, 30, FALSE),
    ('UPC021', NULL, 21, 60.00, 50, FALSE),
    ('UPC005', NULL, 5, 55.00, 60, FALSE),
    ('UPC023', NULL, 23, 70.00, 80, FALSE),
    ('UPC024', NULL, 24, 85.00, 45, FALSE),
    ('UPC025', NULL, 25, 40.00, 20, TRUE),
    ('UPC006', NULL, 6, 48.00, 35, FALSE),
    ('UPC026', NULL, 26, 32.00, 50, FALSE),
    ('UPC027', NULL, 27, 18.50, 70, FALSE),
    ('UPC028', NULL, 28, 55.00, 20, TRUE),
    ('UPC029', NULL, 29, 220.00, 10, FALSE),
    ('UPC030', NULL, 30, 180.00, 10, FALSE),
    ('UPC031', NULL, 31, 110.00, 15, FALSE);

INSERT INTO users (username, password_hash, role, employee_id)
VALUES
    ('manager', '$2a$10$meuvb5wIomHoSdliVxjiWO2wP4FfbiX094SctaUwYz9JjIti.a2IK', 'MANAGER', 'E001'),
    ('cashier', '$2a$10$COs0H4YrU6u2QEhsXDRi/u4V//SHFTADmQKqKQ4hqvWxlTtAmZw4i', 'CASHIER', 'E002');

INSERT INTO Receipt(check_number,id_employee,card_number,print_date, sum_total,vat)
VALUES
    ('CH011', 'E005', 'C001', '2026-04-18 18:20:00', 890.00, 148.33),
    ('CH012', 'E002', 'C008', '2026-04-18 19:10:00', 120.40, 20.07),
    ('CH013', 'E003', NULL,   '2026-04-19 09:05:00', 55.00, 9.17),
    ('CH014', 'E004', 'C002', '2026-04-19 11:30:00', 1450.00, 241.67),
    ('CH015', 'E006', 'C004', '2026-04-19 14:00:00', 312.00, 52.00),
    ('CH016', 'E002', 'C006', '2026-04-20 08:15:00', 78.50, 13.08),
    ('CH017', 'E006', 'C003', '2026-04-20 10:45:00', 210.00, 35.00),
    ('CH018', 'E003', 'C005', '2026-04-20 12:00:00', 1800.00, 300.00),
    ('CH019', 'E005', NULL,   '2026-04-20 15:30:00', 45.00, 7.50),
    ('CH020', 'E002', 'C001', '2026-04-20 17:00:00', 940.00, 156.67),
    ('CH021', 'E004', 'C007', '2026-04-20 18:45:00', 630.00, 105.00),
    ('CH022', 'E003', 'C002', '2026-04-20 19:20:00', 115.00, 19.17),
    ('CH023', 'E005', NULL,   '2026-04-20 20:00:00', 250.00, 41.67),
    ('CH024', 'E002', 'C001', '2026-04-20 20:30:00', 52.00, 8.67),
    ('CH025', 'E006', 'C003', '2026-04-20 21:10:00', 380.00, 63.33);

INSERT INTO Sale (UPC, check_number, product_number, selling_price)
VALUES
    ('UPC018', 'CH011', 2, 250.00),
    ('UPC017', 'CH011', 12, 18.00),
    ('UPC028', 'CH011', 5, 55.00),
    ('UPC002', 'CH012', 2, 28.00),
    ('UPC017', 'CH012', 3, 18.00),
    ('UPC028', 'CH013', 1, 55.00),
    ('UPC029', 'CH014', 4, 220.00),
    ('UPC030', 'CH014', 3, 180.00),
    ('UPC031', 'CH014', 1, 110.00),
    ('UPC020', 'CH015', 2, 95.00),
    ('UPC023', 'CH015', 5, 70.00),
    ('UPC018', 'CH018', 6, 250.00),
    ('UPC003', 'CH018', 4, 75.00),
    ('UPC001', 'CH020', 10, 52.00),
    ('UPC011', 'CH020', 5, 85.00),
    ('UPC023', 'CH021', 4, 70.00),
    ('UPC024', 'CH021', 3, 85.00),
    ('UPC025', 'CH021', 2, 40.00),
    ('UPC018', 'CH023', 1, 250.00),
    ('UPC006', 'CH025', 5, 48.00),
    ('UPC026', 'CH025', 4, 32.00),
    ('UPC027', 'CH025', 3, 18.50),
    ('UPC027', 'CH016', 1, 18.50),
    ('UPC021', 'CH016', 1, 60.00),
    ('UPC023', 'CH017', 3, 70.00),
    ('UPC012', 'CH019', 1, 45.00),
    ('UPC013', 'CH022', 1, 115.00),
    ('UPC001', 'CH024', 1, 52.00);