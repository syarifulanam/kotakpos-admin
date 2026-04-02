CREATE TABLE products (
                            id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                            category_id     BIGINT,
                            name            VARCHAR(255) NOT NULL,
                            barcode         VARCHAR(255),
                            image_url       VARCHAR(500),
                            unit            VARCHAR(50) NOT NULL,
                            sell_price      DECIMAL(15, 2) NOT NULL,
                            cost_price      DECIMAL(15, 2) NOT NULL,
                            stock BIGINT    DEFAULT 0,
                            created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at      TIMESTAMP,
                            deleted_at      TIMESTAMP,
                            CONSTRAINT fk_products_categories FOREIGN KEY (category_id) REFERENCES categories (id)
);