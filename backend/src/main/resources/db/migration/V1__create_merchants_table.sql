CREATE TABLE merchants (
    id UUID PRIMARY KEY,

    merchant_id VARCHAR(50) NOT NULL UNIQUE,

    business_name VARCHAR(255) NOT NULL,

    email VARCHAR(255) NOT NULL UNIQUE,

    api_key_hash TEXT NOT NULL,

    active BOOLEAN NOT NULL,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_merchants_merchant_id
ON merchants (merchant_id);

CREATE INDEX idx_merchants_email
ON merchants (email);