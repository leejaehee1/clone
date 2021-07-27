CREATE DATABASE test DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS Products;

CREATE TABLE Products
(
    prod_id     INT        PRIMARY KEY auto_increment,
    prod_name   VARCHAR(255)    NOT NULL,
    prod_price  INT             NOT NULL
);

INSERT INTO Products (prod_name, prod_price) values ('베베숲 물티슈', 2700);
INSERT INTO Products (prod_name, prod_price) values ('여름 토퍼', 35180);
INSERT INTO Products (prod_name, prod_price) values ('페이크 삭스', 860);
INSERT INTO Products (prod_name, prod_price) values ('우산', 2900);

DROP TABLE IF EXISTS board;

CREATE TABLE board (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title CHAR(255),
    contents CHAR(255)
);