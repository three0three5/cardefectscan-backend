ALTER TABLE image_request
    ADD COLUMN IF NOT EXISTS
        "updated_at" TIMESTAMP NOT NULL DEFAULT now();