ALTER TABLE image_request
    DROP COLUMN IF EXISTS "created_at";
ALTER TABLE image_request
    DROP COLUMN IF EXISTS description;