CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE user_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    provider VARCHAR(50) NOT NULL,
    access_token_encrypted TEXT NOT NULL,
    refresh_token_encrypted TEXT NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    truelayer_account_id VARCHAR(255) NOT NULL,
    account_name VARCHAR(255),
    account_type VARCHAR(100),
    currency VARCHAR(10),
    balance NUMERIC(15,2),
    last_synced_at TIMESTAMP
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    is_default BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    truelayer_transaction_id VARCHAR(255) NOT NULL UNIQUE,
    amount NUMERIC(15,2) NOT NULL,
    currency VARCHAR(10),
    description VARCHAR(500),
    merchant_name VARCHAR(255),
    category_id BIGINT REFERENCES categories(id),
    transaction_date DATE NOT NULL,
    transaction_type VARCHAR(20),
    is_recurring BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE budgets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    category_id BIGINT NOT NULL REFERENCES categories(id),
    monthly_limit NUMERIC(15,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

INSERT INTO categories (name) VALUES
('Groceries'), ('Dining'), ('Transport'), ('Bills'),
('Entertainment'), ('Shopping'), ('Subscriptions'), ('Income'), ('Other');