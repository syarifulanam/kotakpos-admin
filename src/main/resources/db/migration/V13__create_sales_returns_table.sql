CREATE TABLE sales_returns
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    return_number       VARCHAR(50)  UNIQUE NOT NULL,
    sale_id             BIGINT              NOT NULL,
    reason              TEXT,
    total_refund_amount DECIMAL(15, 2)      NOT NULL,
    status              VARCHAR(100)        NOT NULL,
    created_by          BIGINT              NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (sale_id) REFERENCES sales (id),
    FOREIGN KEY (created_by) REFERENCES users (id)
);