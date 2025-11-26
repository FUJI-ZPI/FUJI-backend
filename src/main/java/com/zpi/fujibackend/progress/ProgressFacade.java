package com.zpi.fujibackend.progress;

import com.zpi.fujibackend.user.domain.User;
import jakarta.transaction.Transactional;

import java.time.Instant;

public interface ProgressFacade {
    void increaseUserLevel();

    Integer getDailyStreak();

    @Transactional
    void updateDailyStreak(User user, Instant activityTimestamp);

    long getKanjiLearnedAmount();
}
