CREATE TABLE tariffs (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    price NUMERIC(38, 2),
    period INTEGER
);

CREATE TABLE subscribers (
    id BIGSERIAL PRIMARY KEY,
    phone_number VARCHAR(255) UNIQUE,
    balance NUMERIC(38, 2),
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    tariff_id BIGINT REFERENCES tariffs(id)
);
