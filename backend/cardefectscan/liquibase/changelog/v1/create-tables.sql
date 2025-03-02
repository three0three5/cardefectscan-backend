CREATE TABLE IF NOT EXISTS _user (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    hashed_password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS refresh_session (
    "id" BIGSERIAL PRIMARY KEY,
    "user_id" BIGINT REFERENCES _user(id) ON DELETE CASCADE NOT NULL,
    "refresh_token" uuid NOT NULL UNIQUE,
    "user_agent" CHARACTER VARYING(200) NOT NULL,
    "fingerprint" CHARACTER VARYING(200) NOT NULL,
    "expires_in" BIGINT NOT NULL,
    "created_at" TIMESTAMP NOT NULL DEFAULT now()
);