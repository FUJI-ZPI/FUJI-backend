-- =============================================================================
-- MOCK DATA SCRIPT: User with custom level and learned kanji
-- =============================================================================
-- This script sets up a test user with:
-- - Custom level progress
-- - Some daily streak
-- - All kanji from levels 1 to (level-1) marked as learned
-- =============================================================================

DO
$$
DECLARE
v_user_email VARCHAR := '272741@student.pwr.edu.pl';  -- <- CHANGE EMAIL HERE
    v_target_level
INT := 5;                               -- <- CHANGE LEVEL HERE
    v_user_id
BIGINT;
    v_progress_id
BIGINT;
    v_kanji_id
BIGINT;
    v_kanji_count
INT := 0;
    v_level
INT;
BEGIN
    -- =========================================================================
    -- STEP 1: Get or create the user
    -- =========================================================================
SELECT id
INTO v_user_id
FROM users
WHERE email = v_user_email;

IF
v_user_id IS NULL THEN
        INSERT INTO users (email)
        VALUES (v_user_email)
        RETURNING id INTO v_user_id;
        RAISE
NOTICE 'Created new user with id: %', v_user_id;
ELSE
        RAISE NOTICE 'Found existing user with id: %', v_user_id;
END IF;

    -- =========================================================================
    -- STEP 2: Get or create progress for the user
    -- =========================================================================
SELECT id
INTO v_progress_id
FROM progress
WHERE user_id = v_user_id;

IF
v_progress_id IS NULL THEN
        INSERT INTO progress (user_id, user_level, daily_streak, max_daily_streak, last_streak_updated)
        VALUES (v_user_id, v_target_level, 5, 10, NOW())
        RETURNING id INTO v_progress_id;
        RAISE
NOTICE 'Created new progress with id: %', v_progress_id;
ELSE
UPDATE progress
SET user_level          = v_target_level,
    daily_streak        = 5,
    max_daily_streak    = 10,
    last_streak_updated = NOW()
WHERE id = v_progress_id;
RAISE
NOTICE 'Updated existing progress with id: %', v_progress_id;
END IF;

    -- =========================================================================
    -- STEP 3: Clear existing learned kanji for this user (for clean state)
    -- =========================================================================
DELETE
FROM user_learned_kanji
WHERE progress_id = v_progress_id;
RAISE
NOTICE 'Cleared existing learned kanji';

    -- =========================================================================
    -- STEP 4: Add ALL kanji from levels 1 to (v_target_level - 1) as learned
    -- =========================================================================
FOR v_level IN 1..(v_target_level - 1) LOOP
        FOR v_kanji_id IN
SELECT id
FROM wanikani.kanji
WHERE level = v_level
ORDER BY id
    LOOP
INSERT
INTO user_learned_kanji (progress_id, kanji_id)
VALUES (v_progress_id, v_kanji_id)
ON CONFLICT DO NOTHING;
v_kanji_count
:= v_kanji_count + 1;
END LOOP;
END LOOP;

    RAISE
NOTICE '=== MOCK DATA SETUP COMPLETE ===';
    RAISE
NOTICE 'User email: %', v_user_email;
    RAISE
NOTICE 'User level: %', v_target_level;
    RAISE
NOTICE 'Daily streak: 5';
    RAISE
NOTICE 'Learned kanji (levels 1-%): %', v_target_level - 1, v_kanji_count;

END $$;

-- =============================================================================
-- VERIFICATION QUERIES
-- =============================================================================

-- Check user and progress
SELECT u.id as user_id,
       u.email,
       p.user_level,
       p.daily_streak,
       p.max_daily_streak
FROM users u
         JOIN progress p ON p.user_id = u.id
WHERE u.email = '272741@student.pwr.edu.pl';

-- Check learned kanji with details (first 30)
SELECT k.character,
       k.level,
       k.unicode_character
FROM user_learned_kanji ulk
         JOIN progress p ON p.id = ulk.progress_id
         JOIN users u ON u.id = p.user_id
         JOIN wanikani.kanji k ON k.id = ulk.kanji_id
WHERE u.email = '272741@student.pwr.edu.pl'
ORDER BY k.level, k.id LIMIT 30;

-- Count learned kanji per level
SELECT k.level,
       COUNT(*) as kanji_count
FROM user_learned_kanji ulk
         JOIN progress p ON p.id = ulk.progress_id
         JOIN users u ON u.id = p.user_id
         JOIN wanikani.kanji k ON k.id = ulk.kanji_id
WHERE u.email = '272741@student.pwr.edu.pl'
GROUP BY k.level
ORDER BY k.level;

-- Total learned kanji count
SELECT COUNT(*) as total_learned_kanji
FROM user_learned_kanji ulk
         JOIN progress p ON p.id = ulk.progress_id
         JOIN users u ON u.id = p.user_id
WHERE u.email = '272741@student.pwr.edu.pl';

