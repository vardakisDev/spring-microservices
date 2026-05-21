CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL, -- NOSONAR PostgreSQL uses VARCHAR; VARCHAR2 is Oracle-specific.
    first_name VARCHAR(100) NOT NULL, -- NOSONAR PostgreSQL uses VARCHAR; VARCHAR2 is Oracle-specific.
    last_name VARCHAR(100) NOT NULL, -- NOSONAR PostgreSQL uses VARCHAR; VARCHAR2 is Oracle-specific.
    vip BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE INDEX idx_users_email ON users (LOWER(email));