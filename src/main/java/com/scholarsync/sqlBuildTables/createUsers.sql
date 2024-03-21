CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL,
                       password TEXT NOT NULL,
                       first_name TEXT NOT NULL,
                       last_name TEXT NOT NULL,
                       birth_date DATE NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       credits INTEGER NOT NULL DEFAULT 0,
                       xp INTEGER NOT NULL DEFAULT 0,
                       level_id BIGINT NOT NULL DEFAULT 0,
                       UNIQUE(email)
);
