CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- DROP TABLE IF EXISTS account;
CREATE TABLE IF NOT EXISTS account (
    id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);

-- DROP TABLE IF EXISTS balance;
CREATE TABLE IF NOT EXISTS balance (
    id uuid default uuid_generate_v4(),
    account_id BIGINT NOT NULL,
    balance integer NOT NULL,
    created_by TEXT NOT NULL,
    created_date timestamp with time zone NOT NULL default now(),
    last_modified_date timestamp with time zone,
    last_modified_by TEXT,
    updated_by TEXT,
    PRIMARY KEY(id)
--     CONSTRAINT fk_balance_account FOREIGN KEY(account_id) REFERENCES account(id)
);
