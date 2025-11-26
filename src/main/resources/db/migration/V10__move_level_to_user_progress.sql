BEGIN;

INSERT INTO user_progress (uuid, user_id, user_level, daily_streak, streak_updated)
SELECT gen_random_uuid(),
       u.id,
       COALESCE(u.level, 1),
       0,
       NULL
FROM users u
WHERE NOT EXISTS (
  SELECT 1 FROM user_progress p WHERE p.user_id = u.id
);

ALTER TABLE users DROP COLUMN IF EXISTS level;

COMMIT;