package com.zpi.fujibackend.progress.domain;

import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.kanji.KanjiFacade;
import com.zpi.fujibackend.kanji.domain.Kanji;
import com.zpi.fujibackend.progress.ProgressFacade;
import com.zpi.fujibackend.progress.dto.DailyStreakDto;
import com.zpi.fujibackend.progress.dto.KanjiLearnedDto;
import com.zpi.fujibackend.progress.dto.UserLevelDto;
import com.zpi.fujibackend.user.UserFacade;
import com.zpi.fujibackend.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
class ProgressService implements ProgressFacade {

    private final ProgressRepository progressRepository;
    private final UserFacade userFacade;
    private final KanjiFacade kanjiFacade;

    @Override
    public UserLevelDto getUserLevel() {
        final User user = userFacade.getCurrentUser();
        return progressRepository.findProgressByUser(user)
                .map(Progress::getLevel)
                .map(UserLevelDto::new)
                .orElseThrow(() -> new NotFoundException("Progress not found for user: " + user.getUuid()));
    }

    @Override
    public DailyStreakDto getDailyStreak() {
        final User user = userFacade.getCurrentUser();
        return progressRepository.findProgressByUser(user)
                .map(Progress::getDailyStreak)
                .map(DailyStreakDto::new)
                .orElseThrow(() -> new NotFoundException("Progress not found for user: " + user.getUuid()));
    }

    @Override
    @Transactional
    public void updateDailyStreak(User user, Instant activityTimestamp) {
        Progress progress = progressRepository.findProgressByUser(user)
                .orElseThrow(() -> new NotFoundException("Progress not found"));

        ZoneId zone = ZoneOffset.UTC;
        LocalDate activityDate = activityTimestamp.atZone(zone).toLocalDate();
        LocalDate lastUpdateDate = progress.getLastStreakUpdated() != null
                ? progress.getLastStreakUpdated().atZone(zone).toLocalDate()
                : null;

        if (lastUpdateDate != null) {
            if (lastUpdateDate.isEqual(activityDate.minusDays(1))) {
                progress.setDailyStreak(progress.getDailyStreak() + 1);
            } else if (!lastUpdateDate.isEqual(activityDate)){
                progress.setDailyStreak(1);
            }
        } else {
            progress.setDailyStreak(1);
        }

        progress.setLastStreakUpdated(activityTimestamp);
        progressRepository.save(progress);
    }

    @Override
    public KanjiLearnedDto getKanjiLearnedAmount() {
        final User user = userFacade.getCurrentUser();
        return progressRepository.findProgressByUser(user)
                .map(progress -> new KanjiLearnedDto(progress.getLearnedKanji().size()))
                .orElseThrow(() -> new NotFoundException("Progress not found for user: " + user.getId()));
    }

    @Override
    @Transactional
    public void markKanjiAsLearned(Kanji kanji) {
        User user = userFacade.getCurrentUser();
        Progress progress = progressRepository.findProgressByUser(user)
                .orElseThrow(() -> new NotFoundException("Progress not found for user: " + user.getUuid()));

        progress.getLearnedKanji().add(kanji);

        progressRepository.save(progress);

        Integer currentLevel = progress.getLevel();

        final long learnedKanjiAtCurrentLevel = progress.getLearnedKanji().stream()
                .filter(k -> k.getLevel().equals(currentLevel))
                .count();

        final int totalKanjiInLevel = kanjiFacade.getByLevel(currentLevel).size();

        if (totalKanjiInLevel > 0 && learnedKanjiAtCurrentLevel >= totalKanjiInLevel) {
            increaseUserLevel(progress);
        }
    }

    private void increaseUserLevel(Progress progress) {
        progress.setLevel(progress.getLevel() + 1);
        progressRepository.save(progress);
    }
}
