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
CREATE TABLE products (
                          product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          product_name VARCHAR(255) NOT NULL,
                          price DECIMAL(10, 2) NOT NULL,
                          execution_time DATETIME, -- 예약 구매로 구매 버튼이 활성화 되는 시간
                          product_type VARCHAR(50)
);
CREATE TABLE inventory (
                           inventory_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           product_id BIGINT NOT NULL, -- 외래키 제약 조건 없이 상품 ID를 저장
                           stock_quantity DECIMAL(10, 2) NOT NULL, -- 현재 재고 수량
                           last_update DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
