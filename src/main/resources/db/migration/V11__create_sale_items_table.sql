CREATE TABLE sale_items
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    sale_id     BIGINT          NOT NULL,
    product_id  BIGINT          NOT NULL,
    quantity    BIGINT          NOT NULL,
    sell_price  DECIMAL(15, 2)  NOT NULL,
    subtotal    DECIMAL(15, 2)  NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (sale_id) REFERENCES sales (id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products (id)
);