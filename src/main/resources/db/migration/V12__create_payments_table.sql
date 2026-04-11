CREATE TABLE payments
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    sale_id             BIGINT          NOT NULL,
    amount              DECIMAL(15, 2)  NOT NULL,
    payment_method      VARCHAR(50)     NOT NULL,
    payment_reference   VARCHAR(255)    UNIQUE,
    status              VARCHAR(50)     NOT NULL,
    paid_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (sale_id) REFERENCES sales (id) ON DELETE CASCADE
);