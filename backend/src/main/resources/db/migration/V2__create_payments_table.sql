CREATE TABLE payments (

    id UUID PRIMARY KEY,

    payment_id VARCHAR(50) NOT NULL UNIQUE,

    merchant_id UUID NOT NULL,

    amount NUMERIC(19,2) NOT NULL,

    currency VARCHAR(3) NOT NULL,

    status VARCHAR(20) NOT NULL,

    gateway VARCHAR(20) NOT NULL,

    gateway_payment_id VARCHAR(255) UNIQUE,

    idempotency_key VARCHAR(255) NOT NULL UNIQUE,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_payment_merchant
        FOREIGN KEY (merchant_id)
        REFERENCES merchants(id)
);

CREATE INDEX idx_payment_id
ON payments(payment_id);

CREATE INDEX idx_payment_merchant
ON payments(merchant_id);

CREATE INDEX idx_payment_status
ON payments(status);