CREATE TABLE purchase_items
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_id     BIGINT          NOT NULL,
    product_id      BIGINT          NOT NULL,
    quantity        BIGINT          NOT NULL,
    cost_price      DECIMAL(15, 2)  NOT NULL,
    subtotal        DECIMAL(15, 2)  NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (purchase_id) REFERENCES purchases (id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products (id)
);