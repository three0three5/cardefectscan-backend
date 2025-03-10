CREATE TABLE IF NOT EXISTS _user
(
    id              BIGSERIAL PRIMARY KEY,
    login           VARCHAR(255) NOT NULL UNIQUE,
    hashed_password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS refresh_session
(
    "id"            BIGSERIAL PRIMARY KEY,
    "user_id"       BIGINT REFERENCES _user (id) ON DELETE CASCADE NOT NULL,
    "refresh_token" uuid                                           NOT NULL UNIQUE,
    "fingerprint"   CHARACTER VARYING(200)                         ,
    "created_at"    TIMESTAMP                                      NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS image_request
(
    "image_name" VARCHAR(256) PRIMARY KEY,
    "user_id" BIGINT REFERENCES _user (id) ON DELETE CASCADE NOT NULL,
    "status" VARCHAR(20) NOT NULL
);