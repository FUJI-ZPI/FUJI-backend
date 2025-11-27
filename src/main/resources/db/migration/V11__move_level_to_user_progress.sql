BEGIN;

INSERT INTO progress (uuid, user_id, user_level, daily_streak, max_daily_streak, last_streak_updated)
SELECT gen_random_uuid(),
       u.id,
       COALESCE(u.level, 1),
       0,
       0,
       NULL
FROM users u
WHERE NOT EXISTS (
  SELECT 1 FROM progress p WHERE p.user_id = u.id
);

ALTER TABLE users DROP COLUMN IF EXISTS level;

COMMIT;