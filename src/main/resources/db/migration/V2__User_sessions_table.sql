CREATE TABLE user_sessions (
    id UUID PRIMARY KEY,
    user_id BIGSERIAL NOT NULL,
    refresh_token_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    locale VARCHAR(255),
    device_info VARCHAR(255),
    ip_address VARCHAR(45),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);