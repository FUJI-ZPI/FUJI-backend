CREATE TABLE IF NOT EXISTS user_learned_kanji (
    progress_id BIGINT NOT NULL,
    kanji_id BIGINT NOT NULL,
    PRIMARY KEY (progress_id, kanji_id),
    CONSTRAINT fk_progress FOREIGN KEY (progress_id) REFERENCES progress(id) ON DELETE CASCADE,
    CONSTRAINT fk_kanji FOREIGN KEY (kanji_id) REFERENCES wanikani.kanji(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_ulk_kanji_id ON user_learned_kanji(kanji_id);