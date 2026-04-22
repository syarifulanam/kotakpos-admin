CREATE TABLE sales_return_items
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    sales_return_id BIGINT              NOT NULL,
    product_id      BIGINT              NOT NULL,
    quantity        BIGINT              NOT NULL,
    price           DECIMAL(15, 2)      NOT NULL,
    subtotal        DECIMAL(12, 2)      NOT NULL,
    reason          VARCHAR(255),
    restocked       BOOLEAN DEFAULT FALSE,

    FOREIGN KEY (sales_return_id) REFERENCES sales_returns (id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products (id)
);