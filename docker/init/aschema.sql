CREATE TABLE Category (
                          category_number INT NOT NULL PRIMARY KEY,
                          name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE Product (
                         id_product INT NOT NULL PRIMARY KEY,
                         name VARCHAR(50) NOT NULL,
                         manufacturer VARCHAR(50) NOT NULL,
                         characteristics VARCHAR(100) NOT NULL,
                         category_number INT NOT NULL,
                         FOREIGN KEY (category_number) REFERENCES Category(category_number)
                             ON UPDATE CASCADE
                             ON DELETE NO ACTION
);

CREATE TABLE Employee (
                          id_employee VARCHAR(10) NOT NULL PRIMARY KEY,
                          surname VARCHAR(50) NOT NULL,
                          name VARCHAR(50) NOT NULL,
                          patronymic VARCHAR(50) NULL,
                          role VARCHAR(10) NOT NULL,
                          salary DECIMAL(13,4) NOT NULL CHECK (salary >= 0),
                          date_of_birth DATE NOT NULL,
                          date_of_start DATE NOT NULL,
                          phone_number VARCHAR(13) NOT NULL CHECK (CHAR_LENGTH(phone_number) <= 13),
                          city VARCHAR(50) NOT NULL,
                          street VARCHAR(50) NOT NULL,
                          zip_code VARCHAR(9) NOT NULL
);

CREATE TABLE Customer_Card (
                               card_number VARCHAR(13) NOT NULL PRIMARY KEY,
                               surname VARCHAR(50) NOT NULL,
                               name VARCHAR(50) NOT NULL,
                               patronymic VARCHAR(50) NULL,
                               phone_number VARCHAR(13) NOT NULL CHECK (CHAR_LENGTH(phone_number) <= 13),
                               city VARCHAR(50) NULL,
                               street VARCHAR(50) NULL,
                               zip_code VARCHAR(9) NULL,
                               percent INT NOT NULL CHECK (percent >= 0)
);

CREATE TABLE Store_Product (
                               UPC VARCHAR(12) NOT NULL PRIMARY KEY,
                               UPC_prom VARCHAR(12) NULL,
                               id_product INT NOT NULL,
                               selling_price DECIMAL(13,4) NOT NULL CHECK (selling_price >= 0),
                               products_number INT NOT NULL CHECK (products_number >= 0),
                               promotional_product BOOLEAN NOT NULL,
                               FOREIGN KEY (id_product) REFERENCES Product(id_product)
                                   ON UPDATE CASCADE
                                   ON DELETE NO ACTION,
                               FOREIGN KEY (UPC_prom) REFERENCES Store_Product(UPC)
                                   ON UPDATE CASCADE
                                   ON DELETE NO ACTION
);

CREATE TABLE Receipt (
                         check_number VARCHAR(10) NOT NULL PRIMARY KEY,
                         id_employee VARCHAR(10) NOT NULL,
                         card_number VARCHAR(13) NULL,
                         print_date DATETIME NOT NULL,
                         sum_total DECIMAL(13,4) NOT NULL CHECK (sum_total >= 0),
                         vat DECIMAL(13,4) NOT NULL CHECK (vat >= 0),
                         FOREIGN KEY (id_employee) REFERENCES Employee(id_employee)
                             ON UPDATE CASCADE
                             ON DELETE NO ACTION,
                         FOREIGN KEY (card_number) REFERENCES Customer_Card(card_number)
                             ON UPDATE CASCADE
                             ON DELETE NO ACTION
);

CREATE TABLE Sale (
                      check_number VARCHAR(10) NOT NULL,
                      UPC VARCHAR(12) NOT NULL,
                      product_number INT NOT NULL CHECK (product_number > 0),
                      selling_price DECIMAL(13,4) NOT NULL CHECK (selling_price >= 0),
                      PRIMARY KEY (check_number, UPC),
                      FOREIGN KEY (check_number) REFERENCES Receipt(check_number)
                          ON UPDATE CASCADE
                          ON DELETE NO ACTION,
                      FOREIGN KEY (UPC) REFERENCES Store_Product(UPC)
                          ON UPDATE CASCADE
                          ON DELETE CASCADE
);

CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       employee_id VARCHAR(10),
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       FOREIGN KEY (employee_id) REFERENCES Employee(id_employee)
);

CREATE VIEW check_details_view AS
SELECT
    r.check_number,
    r.print_date,
    r.id_employee,
    r.card_number,
    p.name,
    s.product_number,
    s.selling_price
FROM Receipt r
JOIN Sale s ON r.check_number = s.check_number
JOIN Store_Product sp ON s.UPC = sp.UPC
JOIN Product p ON sp.id_product = p.id_product;