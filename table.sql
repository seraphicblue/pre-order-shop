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

CREATE TABLE `inventory` (
                             `inventory_id` BIGINT NOT NULL AUTO_INCREMENT,
                             `product_id` BIGINT NOT NULL,
                             `stock_quantity` DECIMAL(10,2) NOT NULL,
                             `last_update` DATETIME NOT NULL,
                             PRIMARY KEY (`inventory_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

