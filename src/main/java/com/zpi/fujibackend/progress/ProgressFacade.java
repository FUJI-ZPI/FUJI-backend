package com.zpi.fujibackend.progress;

import com.zpi.fujibackend.chatbot.dto.LearnedKanjiInfoDto;
import com.zpi.fujibackend.kanji.domain.Kanji;
import com.zpi.fujibackend.progress.dto.DailyStreakDto;
import com.zpi.fujibackend.progress.dto.KanjiLearnedDto;
import com.zpi.fujibackend.progress.dto.KanjiAmountRemainingDto;
import com.zpi.fujibackend.progress.dto.UserLevelDto;
import com.zpi.fujibackend.user.domain.User;

import java.time.Instant;
import java.util.List;

public interface ProgressFacade {
    UserLevelDto getUserLevel();

    DailyStreakDto getDailyStreak();

    void updateDailyStreak(User user, Instant activityTimestamp);

    KanjiLearnedDto getKanjiLearnedAmount();

    KanjiAmountRemainingDto getKanjiAmountRemainingForLevel(int level);

    void markKanjiAsLearned(Kanji kanji);

    List<LearnedKanjiInfoDto> getRecentlyLearnedKanjiForChatbot(int limit);
}
