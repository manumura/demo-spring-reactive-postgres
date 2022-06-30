CREATE TABLE IF NOT EXISTS account (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS balance (
    id TEXT PRIMARY KEY,
    balance integer NOT NULL
);
