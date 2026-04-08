CREATE TABLE category (
                          category_number INT PRIMARY KEY,
                          category_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE product (
                         id_product VARCHAR(20) PRIMARY KEY,
                         product_name VARCHAR(100) NOT NULL,
                         manufacturer VARCHAR(100) NOT NULL,
                         characteristics TEXT,
                         category_number INT NOT NULL,
                         FOREIGN KEY (category_number) REFERENCES category(category_number)
);

CREATE TABLE employee (
                          id_employee VARCHAR(20) PRIMARY KEY,
                          surname VARCHAR(50) NOT NULL,
                          name VARCHAR(50) NOT NULL,
                          patronymic VARCHAR(50),
                          position VARCHAR(20) NOT NULL,
                          salary DECIMAL(10,2) NOT NULL CHECK (salary >= 0),
                          start_date DATE NOT NULL,
                          birth_date DATE NOT NULL,
                          phone VARCHAR(13),
                          city VARCHAR(50),
                          street VARCHAR(100),
                          zip_code VARCHAR(10)
);

CREATE TABLE customer_card (
                               card_number VARCHAR(20) PRIMARY KEY,
                               surname VARCHAR(50) NOT NULL,
                               name VARCHAR(50) NOT NULL,
                               patronymic VARCHAR(50),
                               phone VARCHAR(13),
                               city VARCHAR(50),
                               street VARCHAR(100),
                               zip_code VARCHAR(10),
                               discount_percent DECIMAL(5,2) NOT NULL CHECK (discount_percent >= 0)
);

CREATE TABLE store_product (
                               upc VARCHAR(20) PRIMARY KEY,
                               id_product VARCHAR(20) NOT NULL,
                               selling_price DECIMAL(10,2) NOT NULL CHECK (selling_price >= 0),
                               product_count INT NOT NULL CHECK (product_count >= 0),
                               is_promotional BOOLEAN NOT NULL,
                               FOREIGN KEY (id_product) REFERENCES product(id_product)
);

CREATE TABLE receipt (
                         receipt_number VARCHAR(20) PRIMARY KEY,
                         cashier_id VARCHAR(20) NOT NULL,
                         card_number VARCHAR(20),
                         print_date DATETIME NOT NULL,
                         total_sum DECIMAL(10,2) NOT NULL CHECK (total_sum >= 0),
                         vat DECIMAL(10,2) NOT NULL CHECK (vat >= 0),
                         FOREIGN KEY (cashier_id) REFERENCES employee(id_employee),
                         FOREIGN KEY (card_number) REFERENCES customer_card(card_number)
);

CREATE TABLE sale (
                      receipt_number VARCHAR(20) NOT NULL,
                      upc VARCHAR(20) NOT NULL,
                      product_count INT NOT NULL CHECK (product_count > 0),
                      selling_price DECIMAL(10,2) NOT NULL CHECK (selling_price >= 0),
                      PRIMARY KEY (receipt_number, upc),
                      FOREIGN KEY (receipt_number) REFERENCES receipt(receipt_number),
                      FOREIGN KEY (upc) REFERENCES store_product(upc)
);