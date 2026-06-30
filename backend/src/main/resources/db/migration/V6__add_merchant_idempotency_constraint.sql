ALTER TABLE payments
ADD CONSTRAINT uk_payments_merchant_idempotency
UNIQUE (merchant_id, idempotency_key);