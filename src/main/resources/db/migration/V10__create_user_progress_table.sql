CREATE TABLE IF NOT EXISTS progress (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    user_id BIGINT UNIQUE NOT NULL,
    user_level INT NOT NULL DEFAULT 1,
    daily_streak INT NOT NULL DEFAULT 0,
    max_daily_streak INT NOT NULL DEFAULT 0,
    last_streak_updated TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_progress_user_id ON progress(user_id);