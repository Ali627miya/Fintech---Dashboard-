CREATE TABLE recurring_payments (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    merchant_name VARCHAR(255) NOT NULL,
    average_amount NUMERIC(15,2) NOT NULL,
    frequency_days INTEGER NOT NULL,
    last_seen_date DATE NOT NULL,
    next_expected_date DATE
);
