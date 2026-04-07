CREATE TABLE adjustments
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id      BIGINT      NOT NULL,
    type            VARCHAR(50) NOT NULL,
    quantity        BIGINT      NOT NULL,
    note            TEXT,
    created_by      BIGINT      NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY     (product_id) REFERENCES products (id),
    CONSTRAINT      fk_adjustment_user FOREIGN KEY (created_by) REFERENCES users (id)
);