CREATE TABLE payment (
                         payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         payment_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         payment_amount DECIMAL(19, 2) NOT NULL,
                         payment_status VARCHAR(20) NOT NULL,
                         product_id VARCHAR(255) NOT NULL,
                         product_type VARCHAR(255) NOT NULL,
                         payer_id VARCHAR(255) NOT NULL
);
CREATE TABLE products (
                          product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          product_name VARCHAR(255),
                          stock DECIMAL(10, 2),
                          price DECIMAL(10, 2),
                          execution_time DATETIME,
                          product_type VARCHAR(50)
);