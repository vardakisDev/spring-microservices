CREATE TABLE carts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    cart_type VARCHAR(20) NOT NULL, -- NOSONAR PostgreSQL uses VARCHAR; VARCHAR2 is Oracle-specific.
    status VARCHAR(20) NOT NULL, -- NOSONAR PostgreSQL uses VARCHAR; VARCHAR2 is Oracle-specific.
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE cart_items (
    cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_name VARCHAR(150) NOT NULL, -- NOSONAR PostgreSQL uses VARCHAR; VARCHAR2 is Oracle-specific.
    unit_price NUMERIC(12, 2) NOT NULL,
    quantity INTEGER NOT NULL,
    PRIMARY KEY (cart_id, product_id),
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE,
    CONSTRAINT ck_cart_items_quantity_positive CHECK (quantity > 0),
    CONSTRAINT ck_cart_items_unit_price_non_negative CHECK (unit_price >= 0)
);

CREATE TABLE purchases (
    id UUID PRIMARY KEY,
    cart_id UUID NOT NULL,
    user_id UUID NOT NULL,
    cart_type VARCHAR(20) NOT NULL, -- NOSONAR PostgreSQL uses VARCHAR; VARCHAR2 is Oracle-specific.
    total_amount NUMERIC(12, 2) NOT NULL,
    checked_out_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE purchase_items (
    purchase_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_name VARCHAR(150) NOT NULL, -- NOSONAR PostgreSQL uses VARCHAR; VARCHAR2 is Oracle-specific.
    unit_price NUMERIC(12, 2) NOT NULL,
    quantity INTEGER NOT NULL,
    line_total NUMERIC(12, 2) NOT NULL,
    purchased_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (purchase_id, product_id),
    CONSTRAINT fk_purchase_items_purchase FOREIGN KEY (purchase_id) REFERENCES purchases (id) ON DELETE CASCADE,
    CONSTRAINT ck_purchase_items_quantity_positive CHECK (quantity > 0),
    CONSTRAINT ck_purchase_items_unit_price_non_negative CHECK (unit_price >= 0),
    CONSTRAINT ck_purchase_items_line_total_non_negative CHECK (line_total >= 0)
);

CREATE INDEX idx_carts_user_id ON carts (user_id);
CREATE INDEX idx_purchases_user_id ON purchases (user_id);