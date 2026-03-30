CREATE TABLE suppliers
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR (255) NOT NULL,
    phone       VARCHAR (50),
    email       VARCHAR (255),
    address     TEXT,
    city        VARCHAR (100),
    postal_code VARCHAR (20),
    country     VARCHAR (100),
    notes       TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP NULL DEFAULT NULL
);