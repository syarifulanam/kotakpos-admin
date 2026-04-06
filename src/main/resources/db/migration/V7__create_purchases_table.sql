CREATE TABLE purchases
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_id     BIGINT          NOT NULL,
    invoice_number  VARCHAR(100)    NOT NULL,
    total_amount    DECIMAL(15, 2)  NOT NULL,
    status          VARCHAR(100)    NOT NULL,
    cancelled_at    TIMESTAMP NULL,
    cancelled_by    BIGINT NULL,
    cancel_reason   TEXT NULL,
    note            TEXT,
    created_by      BIGINT          NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES suppliers (id),
    FOREIGN KEY (cancelled_by) REFERENCES users (id),
    CONSTRAINT fk_purchase_user FOREIGN KEY (created_by) REFERENCES users (id)
);