CREATE TABLE IF NOT EXISTS wanikani.vocabulary
(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    level integer NOT NULL,
    characters character varying(255),
    document jsonb,
    unicode_characters text[],
    uuid uuid NOT NULL UNIQUE DEFAULT gen_random_uuid()
)
