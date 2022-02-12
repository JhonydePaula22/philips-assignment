CREATE TABLE product (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(50),
    price DECIMAL,
    quantity INTEGER
);

CREATE INDEX ID_INDEX ON product(id);