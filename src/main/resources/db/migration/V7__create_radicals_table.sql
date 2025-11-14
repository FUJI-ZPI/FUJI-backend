CREATE TABLE IF NOT EXISTS wanikani.radicals (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    level INTEGER NOT NULL,
    character VARCHAR(255),
    character_unicode VARCHAR(10),
    slug VARCHAR(255) NOT NULL,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    document JSONB
);