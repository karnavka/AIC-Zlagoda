CREATE TABLE Category (
                          category_number INT NOT NULL PRIMARY KEY,
                          category_name VARCHAR(50) NOT NULL UNIQUE /*хз залишати ли тут юнік але  залишила*/
);

CREATE TABLE Product (
                         id_product INT NOT NULL PRIMARY KEY,
                         product_name VARCHAR(50) NOT NULL,
                         manufacturer VARCHAR(50) NOT NULL,
                         characteristics VARCHAR(100) NOT NULL,
                         category_number INT NOT NULL,
                         FOREIGN KEY (category_number) REFERENCES Category(category_number)
                             ON UPDATE CASCADE
                             ON DELETE SET NULL

);

CREATE TABLE Employee (
                          id_employee VARCHAR(10) NOT NULL PRIMARY KEY,
                          surname VARCHAR(50) NOT NULL,
                          name VARCHAR(50) NOT NULL,
                          patronymic VARCHAR(50),
                          role VARCHAR(10) NOT NULL,
                          salary DECIMAL(13,4) NOT NULL CHECK (salary >= 0),
                          date_of_birth DATE NOT NULL,
                          date_of_start DATE NOT NULL,
                          phone_number VARCHAR(13) NOT NULL,
                          city VARCHAR(50) NOT NULL,
                          street VARCHAR(50) NOT NULL,
                          zip_code VARCHAR(9) NOT NULL
);

CREATE TABLE Customer_Card (
                           card_number VARCHAR(13) NOT NULL PRIMARY KEY,
                           surname VARCHAR(50) NOT NULL,
                           name VARCHAR(50) NOT NULL,
                           patronymic VARCHAR(50) NULL,
                           phone_number VARCHAR(13) NOT NULL,
                           city VARCHAR(50) NULL,
                           street VARCHAR(50) NULL,
                           zip_code VARCHAR(9) NULL,
                           percent INT(5,2) NOT NULL CHECK (percent >= 0)
);

CREATE TABLE Store_Product (
                               UPC VARCHAR(12) NOT NULL PRIMARY KEY,
                               UPC_prom VARCHAR(12) NULL,
                               id_product INT NOT NULL,
                               selling_price DECIMAL(13,4) NOT NULL CHECK (selling_price >= 0),
                               product_count INT NOT NULL CHECK (product_count >= 0),
                               promotional_product BOOLEAN NOT NULL,
                               FOREIGN KEY (id_product) REFERENCES Product(id_product)
                                   ON UPDATE CASCADE
                                   ON DELETE SET NULL,
                               FOREIGN KEY (UPC_prom) REFERENCES Store_Product(UPC_prom)
                                   ON UPDATE CASCADE
                                   ON DELETE SET NULL

);

CREATE TABLE Check (
                         check_number VARCHAR(10) NOT NULL PRIMARY KEY,
                         id_employee VARCHAR(10) NOT NULL, /*? чи краще залишити cashier_id*/
                         card_number VARCHAR(13) NULL,
                         print_date DATETIME NOT NULL,
                         sum_total DECIMAL(13,4) NOT NULL CHECK (sum_total >= 0),
                         vat DECIMAL(13,4) NOT NULL CHECK (vat >= 0),
                         FOREIGN KEY (id_employee) REFERENCES employee(id_employee)
                             ON UPDATE CASCADE
                             ON DELETE SET NULL,
                         FOREIGN KEY (card_number) REFERENCES customer_card(card_number)
                             ON UPDATE CASCADE
                             ON DELETE SET NULL
);
/*Я СПОДІВАЮСЬ Я ПРАВИЛЬНО ЗРОЗУМІЛА ЩО РЕСІПТ ЦЕ ЧЕК. НЕ БИЙТЕ ЯКЩО НЕ ТАК*/
CREATE TABLE Sale (
                      check_number VARCHAR(12) NOT NULL,
                      UPC VARCHAR(10) NOT NULL,
                      product_count INT NOT NULL CHECK (product_count > 0),
                      selling_price DECIMAL(10,2) NOT NULL CHECK (selling_price >= 0),
                      PRIMARY KEY (check_number, upc),
                      FOREIGN KEY (check_number) REFERENCES receipt(check_number)
                          ON UPDATE CASCADE
                          ON DELETE SET NULL,
                      FOREIGN KEY (UPC) REFERENCES Store_Product(UPC)
                          ON UPDATE CASCADE
                          ON DELETE SET NULL
);