ALTER TABLE image_request
    ADD COLUMN IF NOT EXISTS
        "created_at" TIMESTAMP NOT NULL DEFAULT now();

ALTER TABLE image_request
    ADD COLUMN IF NOT EXISTS
        "description" TEXT;