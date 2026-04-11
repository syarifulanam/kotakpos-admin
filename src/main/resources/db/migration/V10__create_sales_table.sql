CREATE TABLE sales
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id     BIGINT          NOT NULL,
    invoice_number  VARCHAR(100)    NOT NULL UNIQUE,
    subtotal        DECIMAL(15, 2)  NOT NULL,
    discount_amount DECIMAL(15, 2)  NULL DEFAULT 0,
    dpp             DECIMAL(15, 2)  NOT NULL,
    tax_rate        DECIMAL(5, 2)   DEFAULT 11.00,
    tax_amount      DECIMAL(15, 2)  NOT NULL,
    total_amount    DECIMAL(15, 2)  NOT NULL,
    status          VARCHAR(100)    NOT NULL,
    note            TEXT,
    created_by      BIGINT          NOT NULL,
    created_at      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP   NULL ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (customer_id) REFERENCES customers (id),
    CONSTRAINT fk_sales_user FOREIGN KEY (created_by) REFERENCES users (id)
);