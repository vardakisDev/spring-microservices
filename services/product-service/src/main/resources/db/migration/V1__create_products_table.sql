CREATE TABLE products (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500) NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_products_name UNIQUE (name),
    CONSTRAINT ck_products_price_positive CHECK (price > 0)
);

CREATE INDEX idx_products_name ON products (LOWER(name));