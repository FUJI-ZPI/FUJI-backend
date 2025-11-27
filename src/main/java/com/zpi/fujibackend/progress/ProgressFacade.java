package com.zpi.fujibackend.progress;

import com.zpi.fujibackend.kanji.domain.Kanji;
import com.zpi.fujibackend.progress.dto.DailyStreakDto;
import com.zpi.fujibackend.progress.dto.KanjiLearnedDto;
import com.zpi.fujibackend.progress.dto.UserLevelDto;
import com.zpi.fujibackend.user.domain.User;
import jakarta.transaction.Transactional;

import java.time.Instant;

public interface ProgressFacade {
    UserLevelDto getUserLevel();

    DailyStreakDto getDailyStreak();

    void updateDailyStreak(User user, Instant activityTimestamp);

    KanjiLearnedDto getKanjiLearnedAmount();

    void markKanjiAsLearned(Kanji kanji);
}
