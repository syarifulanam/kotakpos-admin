CREATE TABLE stock_movements
(
        id              BIGINT AUTO_INCREMENT PRIMARY KEY,
        product_id      BIGINT      NOT NULL,
        type            VARCHAR(50) NOT NULL,
        quantity        BIGINT      NOT NULL CHECK (quantity > 0),
        note            TEXT NULL,
        reference_type  VARCHAR(50) NULL,
        reference_id    BIGINT NULL,
        created_by      BIGINT      NOT NULL,
        created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

        CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES products (id),
        CONSTRAINT fk_stock_user FOREIGN KEY (created_by) REFERENCES users (id));
