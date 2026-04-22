CREATE TABLE refunds
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id      BIGINT          NOT NULL,
    sales_return_id BIGINT          NOT NULL,
    amount          DECIMAL(15, 2)  NOT NULL,
    reason          TEXT,
    created_by      BIGINT          NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (payment_id) REFERENCES payments (id),
    FOREIGN KEY (sales_return_id) REFERENCES sales_returns (id),
    FOREIGN KEY (created_by) REFERENCES users (id)
);