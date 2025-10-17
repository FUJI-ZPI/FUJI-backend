CREATE TABLE IF NOT EXISTS wanikani.kanji
(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    level smallint NOT NULL,
    character character varying(255),
    unicode_character character varying(20),
    document jsonb,
    drawing_data jsonb,
    svg_data jsonb,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid()
)